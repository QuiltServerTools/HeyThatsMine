package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InfoAction;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.literal;

public class InfoCommand implements SubCommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("info")
                .requires(Permissions.require("htm.command.info", true))
                .executes(this::info)
                .build();
    }

    private int info(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        InteractionManager.pendingActions.put(player, new InfoAction());
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);
        return 1;
    }
}
