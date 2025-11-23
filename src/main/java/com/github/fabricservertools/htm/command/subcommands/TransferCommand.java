package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TransferAction;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TransferCommand implements SubCommand {

	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return literal("transfer")
				.requires(Permissions.require("htm.command.transfer", true))
				.then(argument("target", GameProfileArgument.gameProfile())
						.executes(this::transfer))
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int transfer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
        Collection<NameAndId> targets = GameProfileArgument.getGameProfiles(context, "target");

        if (targets.size() > 1) {
            throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.create();
        }

		InteractionManager.pendingActions.put(player, new TransferAction(targets.iterator().next()));
		context.getSource().sendSuccess(() -> HTMComponents.CLICK_TO_SELECT, false);
		return 0;
	}
}
