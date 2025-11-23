package com.github.fabricservertools.htm.command.suggestors;

import com.github.fabricservertools.htm.api.Lock;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

public class LockTypeSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Arrays.stream(Lock.Type.values()).map(Lock.Type::uiName), builder);
	}
}
