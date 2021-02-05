package com.github.fabricservertools.htm.command;

import com.github.fabricservertools.htm.*;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
                        .executes(HTMCommand::trustList)
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

        LiteralCommandNode<ServerCommandSource> untrustNode =
                literal("untrust")
                        .requires(Permissions.require("htm.command.trust", true))
                        .then(argument("target", GameProfileArgumentType.gameProfile())
                                .executes(ctx -> untrust(ctx.getSource(), GameProfileArgumentType.getProfileArgument(ctx, "target").iterator().next(), false))
                                .then(argument("global", StringArgumentType.word())
                                        .suggests((context, builder) -> builder.suggest("global").buildFuture())
                                        .executes(ctx -> untrust(
                                                ctx.getSource(),
                                                GameProfileArgumentType.getProfileArgument(ctx, "target").iterator().next(),
                                                StringArgumentType.getString(ctx, "global").equalsIgnoreCase("global"))
                                        )
                                ))
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

        LiteralCommandNode<ServerCommandSource> flagNode =
                literal("flag")
                        .requires(Permissions.require("htm.command.flag", true))
                        .executes(HTMCommand::flagInfo)
                        .then(argument("type", StringArgumentType.word())
                                .suggests(new FlagTypeSuggestionProvider())
                                .then(argument("value", BoolArgumentType.bool())
                                        .executes(HTMCommand::flag)))
                        .build();

        LiteralCommandNode<ServerCommandSource> persistNode =
                literal("persist")
                        .requires(Permissions.require("htm.command.persist", true))
                                .executes(HTMCommand::persist)
                        .build();

        dispatcher.getRoot().addChild(htmNode);

        htmNode.addChild(setNode);
        htmNode.addChild(removeNode);
        htmNode.addChild(trustNode);
        htmNode.addChild(untrustNode);
        htmNode.addChild(infoNode);
        htmNode.addChild(transferNode);
        htmNode.addChild(flagNode);
        htmNode.addChild(persistNode);
    }

    private static int persist(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        InteractionManager.togglePersist(player);
        if (InteractionManager.persisting.contains(player)) {
            player.sendMessage(new TranslatableText("text.htm.persist").append(new TranslatableText("text.htm.on")), false);
        } else {
            player.sendMessage(new TranslatableText("text.htm.persist").append(new TranslatableText("text.htm.off")), false);
        }
        return 1;
    }

    private static int flagInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();

        InteractionManager.pendingActions.put(player, HTMInteractAction.flag(null, false));
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);

        return 1;
    }

    private static int flag(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        HTMContainerLock.FlagType type;
        ServerPlayerEntity player = context.getSource().getPlayer();
        boolean value = BoolArgumentType.getBool(context, "value");

        try {
            type = HTMContainerLock.FlagType.valueOf(StringArgumentType.getString(context, "type").toUpperCase());
        } catch (IllegalArgumentException e) {
            context.getSource().sendError(new TranslatableText("text.htm.error.flag_type"));
            return -3;
        }

        InteractionManager.pendingActions.put(player, HTMInteractAction.flag(type, value));
        context.getSource().sendFeedback(new TranslatableText("text.htm.select"), false);
        return 1;
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
                source.sendError(new TranslatableText("text.htm.error.already_trusted", gameProfile.getName()));
            }
        } else {
            InteractionManager.pendingActions.put(player, HTMInteractAction.trust(gameProfile, false));
            source.sendFeedback(new TranslatableText("text.htm.select"), false);
        }


        return 1;
    }

    private static int untrust(ServerCommandSource source, GameProfile gameProfile, boolean global) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();

        if (global) {
            GlobalTrustState globalTrustState = player.getServer().getOverworld().getPersistentStateManager().getOrCreate(GlobalTrustState::new, "globalTrust");
            if (globalTrustState.removeTrust(player.getUuid(), gameProfile.getId())) {
                source.sendFeedback(new TranslatableText("text.htm.untrust", gameProfile.getName()).append(new TranslatableText("text.htm.global")), false);
            }
        } else {
            InteractionManager.pendingActions.put(player, HTMInteractAction.trust(gameProfile, true));
            source.sendFeedback(new TranslatableText("text.htm.select"), false);
        }


        return 1;
    }

    private static int trustList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        GlobalTrustState globalTrustState = player.getServer().getOverworld().getPersistentStateManager().getOrCreate(GlobalTrustState::new, "globalTrust");

        String trustedList = globalTrustState.getTrusted().get(player.getUuid())
                .stream()
                .map(a -> player.getServer().getUserCache().getByUuid(a).getName())
                .collect(Collectors.joining(", "));

        player.sendMessage(new TranslatableText("text.htm.trusted.global", trustedList), false);

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
