package com.github.fabricservertools.htm.command;

import com.github.fabricservertools.htm.HTMInteractAction;
import com.github.fabricservertools.htm.InteractionManager;
import com.github.fabricservertools.htm.LockType;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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

public class HTMCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> htmNode =
                literal("htm")
                        .requires(Permissions.require("htm.command.root", true))
                        .build();

        LiteralCommandNode<ServerCommandSource> setNode =
                literal("set")
                        .requires(Permissions.require("htm.command.set", true))
                        .then(argument("type", StringArgumentType.word())
                                .suggests(new LockTypeSuggestionProvider())
                                .executes(HTMCommand::set))
                        .build();

        LiteralCommandNode<ServerCommandSource> removeNode =
                literal("remove")
                        .requires(Permissions.require("htm.command.remove", true))
                        .executes(HTMCommand::remove)
                        .build();

        LiteralCommandNode<ServerCommandSource> trustNode =
                literal("trust")
                        .requires(Permissions.require("htm.command.trust", true))
                        .then(argument("target", GameProfileArgumentType.gameProfile())
                                .executes(HTMCommand::trust))
                        .build();
        
        LiteralCommandNode<ServerCommandSource> infoNode =
                literal("info")
                        .requires(Permissions.require("htm.command.info", true))
                        .executes(HTMCommand::info)
                .build();

        LiteralCommandNode<ServerCommandSource> transferNode =
                literal("transfer")
                        .requires(Permissions.require("htm.command.transfer", true))
                        .then(argument("target", GameProfileArgumentType.gameProfile())
                                .executes(HTMCommand::transfer))
                        .build();

        dispatcher.getRoot().addChild(htmNode);

        htmNode.addChild(setNode);
        htmNode.addChild(removeNode);
        htmNode.addChild(trustNode);
        htmNode.addChild(infoNode);
        htmNode.addChild(transferNode);
    }

    private static int transfer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        GameProfile gameProfile = GameProfileArgumentType.getProfileArgument(context, "target").iterator().next();

        InteractionManager.pendingActions.put(player, HTMInteractAction.transfer(gameProfile));
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);
        return 1;
    }

    private static int info(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        InteractionManager.pendingActions.put(player, HTMInteractAction.info());
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);
        return 1;
    }

    private static int trust(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        GameProfile gameProfile = GameProfileArgumentType.getProfileArgument(context, "target").iterator().next();

        InteractionManager.pendingActions.put(player, HTMInteractAction.trust(gameProfile));
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        InteractionManager.pendingActions.put(player, HTMInteractAction.remove());
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);
        return 1;
    }

    private static int set(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        LockType type;
        ServerPlayerEntity player = context.getSource().getPlayer();

        try {
            type = LockType.valueOf(StringArgumentType.getString(context, "type").toUpperCase());
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(new TranslatableText("text.htm.error.lock_type"));
            return -3;
        }

        InteractionManager.pendingActions.put(player, HTMInteractAction.set(type));
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);
        return 1;
    }
}
