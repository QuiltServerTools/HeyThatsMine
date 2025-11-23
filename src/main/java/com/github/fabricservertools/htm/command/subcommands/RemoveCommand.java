package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.RemoveAction;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class RemoveCommand implements SubCommand {

	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return literal("remove")
				.requires(Permissions.require("htm.command.remove", true))
				.executes(this::remove)
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();

		InteractionManager.pendingActions.put(player, new RemoveAction());
		context.getSource().sendSuccess(() -> HTMComponents.CLICK_TO_SELECT, false);
		return 0;
	}
}
