package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InfoAction;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.ManagerAction;
import com.github.fabricservertools.htm.interactions.TrustAction;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ManagerCommands implements SubCommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("managers")
                .requires(Permissions.require("htm.command.info", true))
                .executes(ManagerCommands::managersList)
                .then(
                        literal("add")
                                .requires(Permissions.require("htm.command.managers.add", true))
                                .then(argument("target", GameProfileArgumentType.gameProfile())
                                              .executes(ctx -> ManagerCommands.editManagers(ctx, false)))
                )
                .then(
                        literal("remove")
                                .requires(Permissions.require("htm.command.managers.remove", true))
                                .then(argument("target", GameProfileArgumentType.gameProfile())
                                              .executes(ctx -> ManagerCommands.editManagers(ctx, true)))
                )
                .build();
    }

    private static int managersList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();


        InteractionManager.pendingActions.put(player, new InfoAction());
        context.getSource().sendFeedback(() -> Text.translatable("text.htm.select"), false);
        return 1;
    }

    private static int editManagers(CommandContext<ServerCommandSource> context, boolean untrust) throws CommandSyntaxException
    {
        ServerCommandSource source = context.getSource();
        Collection<GameProfile> gameProfiles = GameProfileArgumentType.getProfileArgument(context, "target");

        InteractionManager.pendingActions.put(source.getPlayer(), new ManagerAction(gameProfiles, untrust));
        return 1;
    }
}
