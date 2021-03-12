package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TrustAction;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class UntrustCommand implements SubCommand {
	@Override
	public LiteralCommandNode<ServerCommandSource> build() {
		return literal("untrust")
				.requires(Permissions.require("htm.command.trust", true))
				.then(argument("target", GameProfileArgumentType.gameProfile())
						.executes(ctx -> untrust(
								ctx.getSource(),
								GameProfileArgumentType.getProfileArgument(ctx, "target").iterator().next(),
								false
						))
						.then(literal("global")
								.executes(ctx -> untrust(
										ctx.getSource(),
										GameProfileArgumentType.getProfileArgument(ctx, "target").iterator().next(),
										true
								))
						))
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int untrust(ServerCommandSource source, GameProfile gameProfile, boolean global) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayer();

		if (global) {
			GlobalTrustState globalTrustState = Utility.getGlobalTrustState(player.server);
			if (globalTrustState.removeTrust(player.getUuid(), gameProfile.getId())) {
				source.sendFeedback(new TranslatableText("text.htm.untrust", gameProfile.getName()).append(new TranslatableText("text.htm.global")), false);
			} else {
				source.sendFeedback(new TranslatableText("text.htm.error.not_trusted", gameProfile.getName()).append(new TranslatableText("text.htm.global")), false);
			}
		} else {
			InteractionManager.pendingActions.put(player, new TrustAction(gameProfile, true));
			source.sendFeedback(new TranslatableText("text.htm.select"), false);
		}

		return 1;
	}
}
