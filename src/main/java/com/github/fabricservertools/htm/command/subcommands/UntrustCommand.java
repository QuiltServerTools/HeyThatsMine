package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TrustAction;
import com.github.fabricservertools.htm.world.data.GlobalTrustData;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(literal("untrust")
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
                        )
                )
        );
    }

    private int untrust(CommandSourceStack source, Collection<NameAndId> players, boolean global) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();

		if (global) {
			for (NameAndId target : players) {
				GlobalTrustData globalTrustData = Utility.getGlobalTrustData(source.getServer());
				if (globalTrustData.removeTrust(player.getUUID(), target.id())) {
                    source.sendSuccess(() -> HTMComponents.UNTRUST.apply(target.name()).append(CommonComponents.SPACE).append(HTMComponents.GLOBAL), false);
                    return 2;
				} else {
                    source.sendSuccess(() -> HTMComponents.PLAYER_NOT_TRUSTED.apply(target.name()).append(CommonComponents.SPACE).append(HTMComponents.GLOBAL), false);
                    return 3;
				}
			}
		} else {
			InteractionManager.pendingActions.put(player, new TrustAction(players, true));
			source.sendSuccess(() -> HTMComponents.CLICK_TO_SELECT, false);
		}

		return 0;
	}
}
