package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TrustAction;
import com.github.fabricservertools.htm.world.data.GlobalTrustData;
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
		GlobalTrustData globalTrustData = Utility.getGlobalTrustData(context.getSource().getServer());

		String trustedList = globalTrustData.getTrusted().get(player.getUUID())
				.stream()
				.map(uuid -> Utility.getNameFromUUID(uuid, context.getSource().getServer()))
				.collect(Collectors.joining(", "));

		player.displayClientMessage(HTMComponents.TRUSTED_GLOBALLY.apply(Component.literal(trustedList).withStyle(ChatFormatting.WHITE)), false);

		return 0;
	}

	private static int trust(CommandSourceStack source, Collection<NameAndId> players, boolean global) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();

		if (global) {
			for (NameAndId target : players) {
				GlobalTrustData globalTrustData = Utility.getGlobalTrustData(source.getServer());
				if (player.getUUID().equals(target.id())) {
                    source.sendFailure(HTMComponents.CANNOT_TRUST_SELF);
					return 1;
				}

                Component playerName = Component.literal(target.name()).withStyle(ChatFormatting.WHITE);
				if (globalTrustData.addTrust(player.getUUID(), target.id())) {
                    source.sendSuccess(() -> HTMComponents.TRUST.apply(playerName).append(CommonComponents.SPACE).append(HTMComponents.GLOBAL), false);
                    return 2;
				} else {
					source.sendSuccess(() -> HTMComponents.ALREADY_TRUSTED.apply(playerName), false);
                    return 3;
				}
			}
		} else {
			InteractionManager.pendingActions.put(player, new TrustAction(players, false));
			source.sendSuccess(() -> HTMComponents.CLICK_TO_SELECT, false);
		}

		return 4;
	}
}
