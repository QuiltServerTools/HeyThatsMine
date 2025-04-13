package com.github.fabricservertools.htm.command.suggestors;

import com.github.fabricservertools.htm.api.FlagType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class FlagTypeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
		String current = builder.getRemaining().toUpperCase();

		for (FlagType flag : FlagType.values()) {
			if (flag.asString().contains(current.toLowerCase())) {
				builder.suggest(flag.asString().toUpperCase());
			}
		}

		return builder.buildFuture();
	}
}
