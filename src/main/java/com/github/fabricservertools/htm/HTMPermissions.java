package com.github.fabricservertools.htm;

import net.fabricmc.fabric.api.permission.v1.PermissionNode;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;

public final class HTMPermissions {
    public static final PermissionNode<Boolean> COMMAND_ROOT = ofCommand("root");
    public static final PermissionNode<Boolean> COMMAND_FLAG = ofCommand("flag");
    public static final PermissionNode<Boolean> COMMAND_INFO = ofCommand("info");
    public static final PermissionNode<Boolean> COMMAND_PERSIST = ofCommand("persist");
    public static final PermissionNode<Boolean> COMMAND_QUIET = ofCommand("quiet");
    public static final PermissionNode<Boolean> COMMAND_REMOVE = ofCommand("remove");
    public static final PermissionNode<Boolean> COMMAND_SET = ofCommand("set");
    public static final PermissionNode<Boolean> COMMAND_TRANSFER = ofCommand("transfer");
    public static final PermissionNode<Boolean> COMMAND_TRUST = ofCommand("trust");
    public static final PermissionNode<Boolean> ADMIN = of("admin");

    private static PermissionNode<Boolean> ofCommand(String name) {
        return of("command." + name);
    }

    private static PermissionNode<Boolean> of(String name) {
        return PermissionNode.of(HTM.getModdedIdentifier(name));
    }

    public static boolean isHTMAdmin(ServerPlayer player) {
        return player.checkPermission(ADMIN.key(), PermissionLevel.GAMEMASTERS);
    }
}
