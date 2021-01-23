package com.github.fabricservertools.htm.command;

import com.github.fabricservertools.htm.*;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.*;

public class HTMCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> htmNode =
                literal("htm")
                        .build();

        LiteralCommandNode<ServerCommandSource> setNode =
                literal("set")
                        .then(argument("type", StringArgumentType.word())
                                .suggests(new LockTypeSuggestionProvider())
                                .executes(HTMCommand::set))
                        .build();

        LiteralCommandNode<ServerCommandSource> removeNode =
                literal("remove")
                        .executes(HTMCommand::remove)
                        .build();

        LiteralCommandNode<ServerCommandSource> trustNode =
                literal("trust")
                        .then(argument("target", GameProfileArgumentType.gameProfile())
                                .executes(HTMCommand::trust))
                        .build();
        
        LiteralCommandNode<ServerCommandSource> infoNode =
                literal("info")
                .executes(HTMCommand::info)
                .build();

        dispatcher.getRoot().addChild(htmNode);

        htmNode.addChild(setNode);
        htmNode.addChild(removeNode);
        htmNode.addChild(trustNode);
        htmNode.addChild(infoNode);
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
