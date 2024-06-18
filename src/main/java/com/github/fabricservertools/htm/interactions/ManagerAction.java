package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Objects;

public class ManagerAction implements LockInteraction {
    private final Collection<GameProfile> managers;
    private final boolean untrust;

    public ManagerAction(Collection<GameProfile> managers, boolean untrust) {
        this.managers = managers;
        this.untrust = untrust;
    }

    @Override
    public void execute(ServerPlayerEntity player, World world, BlockPos pos, HTMContainerLock lock) {
        if (!lock.isLocked()) {
            player.sendMessage(Text.translatable("text.htm.error.no_lock"), false);
            return;
        }

        // Only owners can add managers.
        if (!lock.isOwner(player)) {
            player.sendMessage(Text.translatable("text.htm.error.not_owner"), false);
            return;
        }

        for (GameProfile manager : managers) {
            if (lock.getOwner() == manager.getId()) {
                player.sendMessage(Text.translatable("text.htm.error.trust_self"), false);
                continue;
            }

            if (untrust) {
                //untrust
                if (lock.removeManager(manager.getId())) {
                    player.sendMessage(Text.translatable("text.htm.untrust_manager", manager.getName()), false);
                } else {
                    player.sendMessage(Text.translatable("text.htm.error.manager_not_trusted", manager.getName()), false);
                }
            } else {
                //trust
                if (lock.addManager(manager.getId())) {
                    player.sendMessage(Text.translatable("text.htm.trust_manager", manager.getName()), false);
                } else {
                    player.sendMessage(Text.translatable("text.htm.error.manager_already_trusted", manager.getName()), false);
                }
            }
        }
    }
}
