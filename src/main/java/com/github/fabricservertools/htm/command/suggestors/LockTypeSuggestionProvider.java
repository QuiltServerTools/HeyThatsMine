package com.github.fabricservertools.htm.command.suggestors;

import com.github.fabricservertools.htm.api.LockType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class LockTypeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
		String current = builder.getRemaining().toUpperCase();

		for (String type : LockType.types()) {
			if (type.contains(current.toLowerCase())) {
				builder.suggest(type.toUpperCase());
			}
		}

		return builder.buildFuture();
	}
}
