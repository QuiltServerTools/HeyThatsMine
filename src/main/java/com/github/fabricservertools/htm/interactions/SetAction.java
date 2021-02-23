package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.HTMRegistry;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SetAction implements LockInteraction {
    private final LockType setType;

    public SetAction(LockType setType) {
        this.setType = setType;
    }

    @Override
    public void execute(ServerPlayerEntity player, World world, BlockPos pos, HTMContainerLock lock) {
        if (lock.isLocked() && !lock.isOwner(player)) {
            player.sendMessage(new TranslatableText("text.htm.error.not_owner"), false);
            return;
        }

        lock.setType(setType, player);
        player.sendMessage(new TranslatableText("text.htm.set", HTMRegistry.getNameFromLock(setType).toUpperCase()), false);
    }
}
