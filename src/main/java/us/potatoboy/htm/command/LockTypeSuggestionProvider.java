package us.potatoboy.htm.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import us.potatoboy.htm.LockType;

import java.util.concurrent.CompletableFuture;

public class LockTypeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String current = builder.getRemaining().toUpperCase();

        for (LockType type : LockType.values()) {
            if (type.name().contains(current.toUpperCase())) {
                builder.suggest(type.name());
            }
        }

        return builder.buildFuture();
    }
}
