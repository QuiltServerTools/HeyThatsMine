package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TrustAction;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TrustCommand implements SubCommand {

	@Override
	public LiteralCommandNode<ServerCommandSource> build() {
		return literal("trust")
				.requires(Permissions.require("htm.command.trust", true))
				.executes(this::trustList)
				.then(argument("target", GameProfileArgumentType.gameProfile())
						.executes(ctx -> trust(ctx.getSource(), GameProfileArgumentType.getProfileArgument(ctx, "target"), false))
						.then(literal("global")
								.executes(ctx -> trust(
										ctx.getSource(), GameProfileArgumentType.getProfileArgument(ctx, "target"), true)
								)
						))
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int trustList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
		GlobalTrustState globalTrustState = Utility.getGlobalTrustState(context.getSource().getServer());

		String trustedList = globalTrustState.getTrusted().get(player.getUuid())
				.stream()
				.map(uuid -> Utility.getNameFromUUID(uuid, context.getSource().getServer()))
				.collect(Collectors.joining(", "));

		player.sendMessage(HTMTexts.TRUSTED_GLOBALLY.apply(trustedList), false);

		return 1;
	}

	private static int trust(ServerCommandSource source, Collection<PlayerConfigEntry> players, boolean global) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrThrow();

		if (global) {
			for (PlayerConfigEntry target : players) {
				GlobalTrustState globalTrustState = Utility.getGlobalTrustState(source.getServer());
				if (player.getUuid().equals(target.id())) {
                    source.sendError(HTMTexts.CANNOT_TRUST_SELF);
					return -1;
				}

				if (globalTrustState.addTrust(player.getUuid(), target.id())) {
                    source.sendFeedback(() -> HTMTexts.TRUST.apply(target.name()).append(ScreenTexts.SPACE).append(HTMTexts.GLOBAL), false);
				} else {
					source.sendFeedback(() -> HTMTexts.ALREADY_TRUSTED.apply(target.name()), false);
				}
			}
		} else {
			InteractionManager.pendingActions.put(player, new TrustAction(players, false));
			source.sendFeedback(() -> HTMTexts.CLICK_TO_SELECT, false);
		}


		return 1;
	}
}
