package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TransferAction;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TransferCommand implements SubCommand {

	@Override
	public LiteralCommandNode<ServerCommandSource> build() {
		return literal("transfer")
				.requires(Permissions.require("htm.command.transfer", true))
				.then(argument("target", GameProfileArgumentType.gameProfile())
						.executes(this::transfer))
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int transfer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
        Collection<PlayerConfigEntry> targets = GameProfileArgumentType.getProfileArgument(context, "target");

        if (targets.size() > 1) {
            throw EntityArgumentType.TOO_MANY_PLAYERS_EXCEPTION.create();
        }

		InteractionManager.pendingActions.put(player, new TransferAction(targets.iterator().next()));
		context.getSource().sendFeedback(() -> HTMTexts.CLICK_TO_SELECT, false);
		return 1;
	}
}
