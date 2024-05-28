package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.command.suggestors.LockGroupSuggestionProvider;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.TrustAction;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.mixin.command.ArgumentTypesAccessor;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Defines commands which exist under the group node
 */
public class GroupCommands implements SubCommand {

    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return literal("group")
                .requires(Permissions.require("htm.command.group", true))
                .executes(ctx -> groupList(ctx, true))
                .then(literal("list")
                                .executes(ctx -> groupList(ctx, true))
                                .then(
                                        argument("owner", GameProfileArgumentType.gameProfile())
                                                .requires(Permissions.require("htm.command.group.listOther", false))
                                                .executes(ctx -> groupList(ctx, false))
                                )
                )
                .then(literal("create")
                                .requires(Permissions.require("htm.command.group.create", true))
                                .then(
                                        argument("name", StringArgumentType.string())
                                                .executes(ctx -> createGroup(ctx.getSource(), StringArgumentType.getString(ctx, "name")))
                                                .then(
                                                        argument("owner", GameProfileArgumentType.gameProfile())
                                                                .requires(Permissions.require("htm.command.group.create.other"))
                                                                .executes(ctx -> createGroupFor(
                                                                        ctx.getSource(),
                                                                        StringArgumentType.getString(ctx, "name"),
                                                                        GameProfileArgumentType.getProfileArgument(ctx, "owner")
                                                                                               .stream()
                                                                                               .findFirst()
                                                                                               .orElseThrow()
                                                                                               .getId()
                                                                        )
                                                                )
                                                )
                                )
                )
                .then(literal("delete")
                              .requires(Permissions.require("htm.command.group.delete"))
                )
                .then(literal("search")
                              .requires(Permissions.require("htm.command.group.search"))
                        // TODO: By name
                        // TODO: By owner
                        // TODO: By UUID
                )
                .then(literal("edit")
                                .requires(Permissions.require("htm.command.group.edit", true))
                                .then(literal("name").then(
                                        argument("group", StringArgumentType.string())
                                                .suggests(new LockGroupSuggestionProvider(false))
                                                        .then(
                                                                argument("newName", StringArgumentType.string())
                                                                        .executes(ctx -> editGroupName(
                                                                                          ctx.getSource(),
                                                                                          StringArgumentType.getString(ctx, "group"),
                                                                                          StringArgumentType.getString(ctx, "newName")
                                                                                  )
                                                                        )
                                                        )

                                        )
                                )
                                .then(literal("trusted").then(
                                        argument("group", StringArgumentType.string())
                                                .suggests(new LockGroupSuggestionProvider(true))
                                                .then(literal("add")
                                                              .requires()
                                                              .then(
                                                                      argument("target", GameProfileArgumentType.gameProfile())
                                                ))
                                                .then(literal("remove"))
                                      )
                                )
                                .then(literal("managers"))
                                .then(literal("transfer"))
                                .then(
                                        literal("other")
                                                .requires(Permissions.require("htm.command.group.edit.others"))
                                                .then(
                                                    argument("owner", GameProfileArgumentType.gameProfile())
                                                        .then(literal("name").then(
                                                             argument("group", StringArgumentType.string()
                                                                     //.suggests(new LockGroupSuggestionProvider(false, GameProfileArgumentType.getProfileArgument()))
                                                             )
                                                        )
                                                        .then(literal("trusted"))
                                                        .then(literal("managers"))
                                                        .then(literal("transfer"))
                                                )
                                )
                )
                .build();



    }

    private int groupList(CommandContext<ServerCommandSource> context, boolean showForSelf) throws CommandSyntaxException {
        return 1;
    }

    private int createGroup(ServerCommandSource source, String groupName) throws CommandSyntaxException {
        return createGroupFor(source, groupName, source.getPlayerOrThrow().getUuid());
    }

    private int createGroupFor(ServerCommandSource source, String groupName, UUID owner) {
        return 1;
    }

    private int editGroupName(ServerCommandSource source, String groupName, String newName) {
        return 1;
    }

    private void hasAddRemoveTrustPermissions(ServerCommandSource source, String groupName) {

    }
}
