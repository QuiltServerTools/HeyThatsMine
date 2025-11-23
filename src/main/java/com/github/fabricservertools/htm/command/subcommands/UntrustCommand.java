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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class UntrustCommand implements SubCommand {

	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return literal("untrust")
				.requires(Permissions.require("htm.command.trust", true))
				.then(argument("target", GameProfileArgument.gameProfile())
						.executes(ctx -> untrust(
								ctx.getSource(),
								GameProfileArgument.getGameProfiles(ctx, "target"),
								false
						))
						.then(literal("global")
								.executes(ctx -> untrust(
										ctx.getSource(),
										GameProfileArgument.getGameProfiles(ctx, "target"),
										true
								))
						))
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int untrust(CommandSourceStack source, Collection<NameAndId> players, boolean global) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();

		if (global) {
			for (NameAndId target : players) {
				GlobalTrustState globalTrustState = Utility.getGlobalTrustState(source.getServer());
				if (globalTrustState.removeTrust(player.getUUID(), target.id())) {
                    source.sendSuccess(() -> HTMTexts.UNTRUST.apply(target.name()).append(CommonComponents.SPACE).append(HTMTexts.GLOBAL), false);
				} else {
                    source.sendSuccess(() -> HTMTexts.PLAYER_NOT_TRUSTED.apply(target.name()).append(CommonComponents.SPACE).append(HTMTexts.GLOBAL), false);
				}
			}
		} else {
			InteractionManager.pendingActions.put(player, new TrustAction(players, true));
			source.sendSuccess(() -> HTMTexts.CLICK_TO_SELECT, false);
		}

		return 1;
	}
}
