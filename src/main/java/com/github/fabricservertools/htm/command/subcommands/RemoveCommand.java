package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.RemoveAction;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class RemoveCommand implements SubCommand {
	@Override
	public LiteralCommandNode<ServerCommandSource> build() {
		return literal("remove")
				.requires(Permissions.require("htm.command.remove", true))
				.executes(this::remove)
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();

		InteractionManager.pendingActions.put(player, new RemoveAction());
		context.getSource().sendFeedback(() -> HTMTexts.CLICK_TO_SELECT, false);
		return 1;
	}
}
