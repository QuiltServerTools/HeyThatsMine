package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TransferAction;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TransferCommand implements SubCommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("transfer")
                .requires(Permissions.require("htm.command.transfer", true))
                .then(argument("target", GameProfileArgumentType.gameProfile())
                        .executes(this::transfer))
                .build();
    }

    private int transfer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        GameProfile gameProfile = GameProfileArgumentType.getProfileArgument(context, "target").iterator().next();

        InteractionManager.pendingActions.put(player, new TransferAction(gameProfile));
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);
        return 1;
    }
}
