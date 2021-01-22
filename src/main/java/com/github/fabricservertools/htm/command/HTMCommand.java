package com.github.fabricservertools.htm.command;

import com.github.fabricservertools.htm.HTMContainerLock;
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
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import com.github.fabricservertools.htm.LockType;
import com.github.fabricservertools.htm.LockableObject;

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
                                .then(argument("pos", BlockPosArgumentType.blockPos())
                                        .executes(HTMCommand::set)))
                        .build();

        LiteralCommandNode<ServerCommandSource> removeNode =
                literal("remove")
                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                .executes(HTMCommand::remove))
                        .build();

        LiteralCommandNode<ServerCommandSource> trustNode =
                literal("trust")
                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                .then(argument("target", GameProfileArgumentType.gameProfile())
                                        .executes(HTMCommand::trust)))
                                .build();

        dispatcher.getRoot().addChild(htmNode);

        htmNode.addChild(setNode);
        htmNode.addChild(removeNode);
        htmNode.addChild(trustNode);
    }

    private static int trust(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
        GameProfile gameProfile = GameProfileArgumentType.getProfileArgument(context, "target").iterator().next();

        HTMContainerLock lock = getLock(context, player, pos);
        if (lock == null) return -1;

        if (lock.getType() == null) {
            context.getSource().sendError(new LiteralText("Object not locked"));
            return -1;
        }

        if (lock.getOwner() != player.getUuid()) {
            context.getSource().sendError(new LiteralText("Not allowed to modify that object"));
            return -2;
        }

        lock.addTrust(gameProfile.getId());
        context.getSource().sendFeedback(new LiteralText("Trusted " + gameProfile.getName()), false);
        return 1;
    }

    private static int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");

        HTMContainerLock lock = getLock(context, player, pos);
        if (lock == null) return -1;
        if (lock.getOwner() != player.getUuid()) {
            context.getSource().sendError(new LiteralText("Not allowed to unlock that object"));
            return -2;
        }

        lock.remove();
        context.getSource().sendFeedback(new LiteralText("Object unlocked"), false);
        return 1;
    }



    private static int set(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        LockType type;
        ServerPlayerEntity player = context.getSource().getPlayer();
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");

        try {
            type = LockType.valueOf(StringArgumentType.getString(context, "type").toUpperCase());
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(new LiteralText("Invalid lock type"));
            return -3;
        }

        HTMContainerLock lock = getLock(context, player, pos);
        if (lock == null) return -1;

        if (lock.getType() != null) {
            context.getSource().sendError(new LiteralText("Object already locked"));
            return -2;
        }

        lock.setType(type, player);
        context.getSource().sendFeedback(new LiteralText("Object locked"), false);
        return 1;
    }

    @Nullable
    private static HTMContainerLock getLock(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, BlockPos pos) {
        BlockEntity blockEntity = player.getServerWorld().getBlockEntity(pos);
        if (!(blockEntity instanceof LockableContainerBlockEntity) || blockEntity == null) {
            context.getSource().sendError(new LiteralText("Object not lockable"));
            return null;
        }

        HTMContainerLock lock = ((LockableObject) blockEntity).getLock();
        return lock;
    }
}
