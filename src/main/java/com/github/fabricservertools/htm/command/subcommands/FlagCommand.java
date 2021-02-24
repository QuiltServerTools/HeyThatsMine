package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.command.FlagTypeSuggestionProvider;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.FlagAction;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Pair;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FlagCommand implements SubCommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("flag")
                .requires(Permissions.require("htm.command.flag", true))
                .executes(this::flagInfo)
                .then(argument("type", StringArgumentType.word())
                        .suggests(new FlagTypeSuggestionProvider())
                        .then(argument("value", BoolArgumentType.bool())
                                .executes(this::flag)))
                .build();
    }

    private int flagInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        InteractionManager.pendingActions.put(player, new FlagAction(Optional.empty()));
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);

        return 1;
    }

    private int flag(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String type;
        ServerPlayerEntity player = context.getSource().getPlayer();
        boolean value = BoolArgumentType.getBool(context, "value");

        try {
            type = StringArgumentType.getString(context, "type".toLowerCase());
        } catch (IllegalArgumentException e) {
            context.getSource().sendFeedback(new TranslatableText("text.htm.error.flag_type"), false);
            return -3;
        }

        InteractionManager.pendingActions.put(player, new FlagAction(Optional.of(new Pair<>(type, value))));
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);
        return 1;
    }
}
