package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RemoveAction implements LockInteraction {
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

		lock.remove();
		player.sendMessage(new TranslatableText("text.htm.unlocked"), false);
	}
}
