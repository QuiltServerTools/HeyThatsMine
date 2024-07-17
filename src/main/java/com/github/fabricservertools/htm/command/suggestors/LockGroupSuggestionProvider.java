package com.github.fabricservertools.htm.command.suggestors;

import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.ProtectionGroup;
import com.github.fabricservertools.htm.world.data.LockGroupState;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Provides suggestions for the names of Lock Groups.
 */
public class LockGroupSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    private final boolean suggestManagedGroups;
    private Set<ProtectionGroup> groupsForOwner;
    private boolean initialized = false;
    private Optional<ServerPlayerEntity> suggestedOwner;

    public LockGroupSuggestionProvider(boolean suggestManagedGroups)
    {
        this(suggestManagedGroups, null);
    }

    public LockGroupSuggestionProvider(
            boolean suggestManagedGroups,
            Optional<ServerPlayerEntity> owner
    ) {
        this.suggestManagedGroups = suggestManagedGroups;
        this.suggestedOwner = owner;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<ServerCommandSource> context,
            SuggestionsBuilder builder
    ) throws CommandSyntaxException {
        init(context.getSource());
        String current = builder.getRemaining();

        for (ProtectionGroup protectionGroup : groupsForOwner) {
            if (protectionGroup.getName().contains(current)) {
                builder.suggest(protectionGroup.getName());
            }
        }

        return builder.buildFuture();
    }

    private void init(ServerCommandSource source) throws CommandSyntaxException {
        if (initialized) {
            return;
        }
        LockGroupState lockGroupState = Utility.getLockGroupState(source.getServer());
        ServerPlayerEntity owner = suggestedOwner.orElse(source.getPlayerOrThrow());
        if (suggestManagedGroups) {
            groupsForOwner = lockGroupState.getGroups()
                                           .values()
                                           .stream()
                                           .filter(group -> group.getOwner() == owner.getUuid() || group.getManagers().contains(owner.getUuid()))
                                           .collect(Collectors.toSet());
        } else {
            groupsForOwner = lockGroupState.getGroups()
                                           .values()
                                           .stream()
                                           .filter(group -> group.getOwner() == owner.getUuid())
                                           .collect(Collectors.toSet());
        }

        initialized = true;
    }
}
