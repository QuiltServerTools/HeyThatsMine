package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TrustAction implements LockInteraction {
    private final GameProfile trustPlayer;
    private final boolean untrust;

    public TrustAction(GameProfile trustPlayer, boolean untrust) {
        this.trustPlayer = trustPlayer;
        this.untrust = untrust;
    }

    @Override
    public void execute(ServerPlayerEntity player, World world, BlockPos pos, HTMContainerLock lock) {
        if (!lock.isLocked()) {
            player.sendMessage(new TranslatableText("text.htm.error.no_lock"), false);
            return;
        }

        if (!lock.isOwner(player)) {
            player.sendMessage(new TranslatableText("text.htm.error.not_owner"), false);
            return;
        }

        if (lock.getOwner() == trustPlayer.getId()) {
            player.sendMessage(new TranslatableText("text.htm.error.trust_self"), false);
            return;
        }

        if (untrust) {
            //untrust
            if (lock.removeTrust(trustPlayer.getId())) {
                player.sendMessage(new TranslatableText("text.htm.untrust", trustPlayer.getName()), false);
            } else {
                player.sendMessage(new TranslatableText("text.htm.error.not_trusted", trustPlayer.getName()), false);
            }
        } else {
            //trust
            if (lock.addTrust(trustPlayer.getId())){
                player.sendMessage(new TranslatableText("text.htm.trust", trustPlayer.getName()), false);
            } else {
                player.sendMessage(new TranslatableText("text.htm.error.already_trusted", trustPlayer.getName()), false);
            }
        }
    }
}
