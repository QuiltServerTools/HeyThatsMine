package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.HTMRegistry;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.stream.Collectors;

public class InfoAction implements LockInteraction {
    @Override
    public void execute(ServerPlayerEntity player, World world, BlockPos pos, HTMContainerLock lock) {
        if (!lock.isLocked()) {
            player.sendMessage(new TranslatableText("text.htm.error.no_lock"), false);
            return;
        }

        GameProfile owner = world.getServer().getUserCache().getByUuid(lock.getOwner());

        player.sendMessage(new TranslatableText("text.htm.divider"), false);
        player.sendMessage(new TranslatableText("text.htm.type", HTMRegistry.getNameFromLock(lock.getType()).toUpperCase()), false);
        player.sendMessage(new TranslatableText("text.htm.owner", owner.getName()), false);
        if (owner.getId().equals(player.getUuid())) {
            String trustedList = lock.getTrusted()
                    .stream()
                    .map(a -> world.getServer().getUserCache().getByUuid(a).getName())
                    .collect(Collectors.joining(", "));

            player.sendMessage(new TranslatableText("text.htm.trusted", trustedList), false);
            lock.getType().onInfo(player, lock);
        }
        player.sendMessage(new TranslatableText("text.htm.divider"), false);
    }
}
