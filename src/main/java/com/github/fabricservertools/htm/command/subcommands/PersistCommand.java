package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class PersistCommand implements SubCommand {
	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return literal("persist")
				.requires(Permissions.require("htm.command.persist", true))
				.executes(this::persist)
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int persist(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();

		InteractionManager.togglePersist(player);
		if (InteractionManager.persisting.contains(player.getUUID())) {
			context.getSource().sendSuccess(() -> HTMTexts.TOGGLE_PERSIST_ON, false);
		} else {
			context.getSource().sendSuccess(() -> HTMTexts.TOGGLE_PERSIST_OFF, false);
		}
		return 1;
	}
}
