package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TrustAction;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

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
                        .executes(ctx -> trust(ctx.getSource(), GameProfileArgumentType.getProfileArgument(ctx, "target").iterator().next(), false))
                        .then(argument("global", StringArgumentType.word())
                                .suggests((context, builder) -> builder.suggest("global").buildFuture())
                                .executes(ctx -> trust(
                                        ctx.getSource(),
                                        GameProfileArgumentType.getProfileArgument(ctx, "target").iterator().next(),
                                        StringArgumentType.getString(ctx, "global").equalsIgnoreCase("global"))
                                )
                        ))
                .build();
    }

    private int trustList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        GlobalTrustState globalTrustState = player.getServer().getOverworld().getPersistentStateManager().getOrCreate(GlobalTrustState::new, "globalTrust");

        String trustedList = globalTrustState.getTrusted().get(player.getUuid())
                .stream()
                .map(a -> player.getServer().getUserCache().getByUuid(a).getName())
                .collect(Collectors.joining(", "));

        player.sendMessage(new TranslatableText("text.htm.trusted.global", trustedList), false);

        return 1;
    }

    private static int trust(ServerCommandSource source, GameProfile gameProfile, boolean global) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();

        if (global) {
            GlobalTrustState globalTrustState = player.getServer().getOverworld().getPersistentStateManager().getOrCreate(GlobalTrustState::new, "globalTrust");
            if (player.getUuid().equals(gameProfile.getId())) {
                player.sendMessage(new TranslatableText("text.htm.error.trust_self"), false);
                return -1;
            }

            if (globalTrustState.addTrust(player.getUuid(), gameProfile.getId())) {
                source.sendFeedback(new TranslatableText("text.htm.trust", gameProfile.getName()).append(new TranslatableText("text.htm.global")), false);
            } else {
                source.sendFeedback(new TranslatableText("text.htm.error.already_trusted", gameProfile.getName()), false);
            }
        } else {
            InteractionManager.pendingActions.put(player, new TrustAction(gameProfile, false));
            source.sendFeedback(new TranslatableText("text.htm.select"), false);
        }


        return 1;
    }
}
