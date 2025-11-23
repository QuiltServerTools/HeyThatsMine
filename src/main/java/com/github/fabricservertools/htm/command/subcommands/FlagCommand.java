package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.command.suggestors.FlagTypeSuggestionProvider;
import com.github.fabricservertools.htm.interactions.FlagAction;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import java.util.Optional;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FlagCommand implements SubCommand {

	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return literal("flag")
				.requires(Permissions.require("htm.command.flag", true))
				.executes(this::flagInfo)
				.then(argument("type", StringArgumentType.word())
						.suggests(new FlagTypeSuggestionProvider())
						.then(argument("value", BoolArgumentType.bool())
								.executes(context -> flag(context, false)))
                        .executes(context -> flag(context, true)))
				.build();
	}

	private int flagInfo(CommandContext<CommandSourceStack> context) {
		ServerPlayer player = context.getSource().getPlayer();

		InteractionManager.pendingActions.put(player, new FlagAction(Optional.empty()));
		context.getSource().sendSuccess(() -> HTMTexts.CLICK_TO_SELECT, false);

		return 1;
	}

	private int flag(CommandContext<CommandSourceStack> context, boolean unset) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayer();
		FlagType type = FlagType.fromString(StringArgumentType.getString(context, "type"));
		Boolean value = unset ? null : BoolArgumentType.getBool(context, "value");

		if (type == null) {
			throw new SimpleCommandExceptionType(HTMTexts.INVALID_FLAG_TYPE).create();
		}

		InteractionManager.pendingActions.put(player, new FlagAction(Optional.of(new Tuple<>(type, value))));
		context.getSource().sendSuccess(() -> HTMTexts.CLICK_TO_SELECT, false);
		return 1;
	}
}
