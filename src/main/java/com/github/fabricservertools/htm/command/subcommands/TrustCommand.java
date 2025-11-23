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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import java.util.Collection;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TrustCommand implements SubCommand {

	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return literal("trust")
				.requires(Permissions.require("htm.command.trust", true))
				.executes(this::trustList)
				.then(argument("target", GameProfileArgument.gameProfile())
						.executes(ctx -> trust(ctx.getSource(), GameProfileArgument.getGameProfiles(ctx, "target"), false))
						.then(literal("global")
								.executes(ctx -> trust(
										ctx.getSource(), GameProfileArgument.getGameProfiles(ctx, "target"), true)
								)
						))
				.build();
	}

	@SuppressWarnings("SameReturnValue")
	private int trustList(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		GlobalTrustState globalTrustState = Utility.getGlobalTrustState(context.getSource().getServer());

		String trustedList = globalTrustState.getTrusted().get(player.getUUID())
				.stream()
				.map(uuid -> Utility.getNameFromUUID(uuid, context.getSource().getServer()))
				.collect(Collectors.joining(", "));

		player.displayClientMessage(HTMTexts.TRUSTED_GLOBALLY.apply(Component.literal(trustedList).withStyle(ChatFormatting.WHITE)), false);

		return 1;
	}

	private static int trust(CommandSourceStack source, Collection<NameAndId> players, boolean global) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();

		if (global) {
			for (NameAndId target : players) {
				GlobalTrustState globalTrustState = Utility.getGlobalTrustState(source.getServer());
				if (player.getUUID().equals(target.id())) {
                    source.sendFailure(HTMTexts.CANNOT_TRUST_SELF);
					return -1;
				}

                Component playerName = Component.literal(target.name()).withStyle(ChatFormatting.WHITE);
				if (globalTrustState.addTrust(player.getUUID(), target.id())) {
                    source.sendSuccess(() -> HTMTexts.TRUST.apply(playerName).append(CommonComponents.SPACE).append(HTMTexts.GLOBAL), false);
				} else {
					source.sendSuccess(() -> HTMTexts.ALREADY_TRUSTED.apply(playerName), false);
				}
			}
		} else {
			InteractionManager.pendingActions.put(player, new TrustAction(players, false));
			source.sendSuccess(() -> HTMTexts.CLICK_TO_SELECT, false);
		}


		return 1;
	}
}
