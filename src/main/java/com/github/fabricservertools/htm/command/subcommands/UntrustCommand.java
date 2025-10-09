package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TrustAction;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

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
								GameProfileArgumentType.getProfileArgument(ctx, "target"),
								false
						))
						.then(literal("global")
								.executes(ctx -> untrust(
										ctx.getSource(),
										GameProfileArgumentType.getProfileArgument(ctx, "target"),
										true
								))
						))
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int untrust(ServerCommandSource source, Collection<PlayerConfigEntry> players, boolean global) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrThrow();

		if (global) {
			for (PlayerConfigEntry target : players) {
				GlobalTrustState globalTrustState = Utility.getGlobalTrustState(source.getServer());
				if (globalTrustState.removeTrust(player.getUuid(), target.id())) {
                    source.sendFeedback(() -> HTMTexts.UNTRUST.apply(target.name()).append(ScreenTexts.SPACE).append(HTMTexts.GLOBAL), false);
				} else {
                    source.sendFeedback(() -> HTMTexts.PLAYER_NOT_TRUSTED.apply(target.name()).append(ScreenTexts.SPACE).append(HTMTexts.GLOBAL), false);
				}
			}
		} else {
			InteractionManager.pendingActions.put(player, new TrustAction(players, true));
			source.sendFeedback(() -> HTMTexts.CLICK_TO_SELECT, false);
		}

		return 1;
	}
}
