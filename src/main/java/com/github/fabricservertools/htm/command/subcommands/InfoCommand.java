package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InfoAction;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class InfoCommand implements SubCommand {

	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return literal("info")
				.requires(Permissions.require("htm.command.info", true))
				.executes(this::info)
				.build();
	}

	private int info(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();

		InteractionManager.pendingActions.put(player, new InfoAction());
		context.getSource().sendSuccess(() -> HTMComponents.CLICK_TO_SELECT, false);
		return 1;
	}
}
