package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static net.minecraft.server.command.CommandManager.literal;

public class PersistCommand implements SubCommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("persist")
                .requires(Permissions.require("htm.command.persist", true))
                .executes(this::persist)
                .build();
    }

    private int persist(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        InteractionManager.togglePersist(player);
        if (InteractionManager.persisting.contains(player)) {
            player.sendMessage(new TranslatableText("text.htm.persist").append(new TranslatableText("text.htm.on")), false);
        } else {
            player.sendMessage(new TranslatableText("text.htm.persist").append(new TranslatableText("text.htm.off")), false);
        }
        return 1;
    }
}
