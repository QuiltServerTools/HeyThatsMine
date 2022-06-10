package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class QuietCommand implements SubCommand {
	@Override
	public LiteralCommandNode<ServerCommandSource> build() {
		return literal("quiet")
				.requires(Permissions.require("htm.command.quiet", true))
				.executes(this::quiet)
				.build();
	}

	private int quiet(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();

		InteractionManager.toggleNoMessage(player);
		if (InteractionManager.noMessage.contains(player.getUuid())) {
			context.getSource().sendFeedback(Text.translatable("text.htm.no_msg").append(Text.translatable("text.htm.on")), false);
		} else {
			context.getSource().sendFeedback(Text.translatable("text.htm.no_msg").append(Text.translatable("text.htm.off")), false);
		}
		return 1;
	}
}
