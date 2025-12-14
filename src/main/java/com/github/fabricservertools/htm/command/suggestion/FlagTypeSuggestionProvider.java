package com.github.fabricservertools.htm.command.suggestion;

import com.github.fabricservertools.htm.api.FlagType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;

public class FlagTypeSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
		String current = builder.getRemaining().toUpperCase();

		for (FlagType flag : FlagType.values()) {
			if (flag.getSerializedName().contains(current.toLowerCase())) {
				builder.suggest(flag.getSerializedName().toUpperCase());
			}
		}

		return builder.buildFuture();
	}
}
