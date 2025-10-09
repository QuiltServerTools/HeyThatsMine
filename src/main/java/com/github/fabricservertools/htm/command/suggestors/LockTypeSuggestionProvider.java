package com.github.fabricservertools.htm.command.suggestors;

import com.github.fabricservertools.htm.api.Lock;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class LockTypeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Arrays.stream(Lock.Type.values()).map(Lock.Type::uiName), builder);
	}
}
