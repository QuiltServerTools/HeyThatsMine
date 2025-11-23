package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class RemoveAction implements LockInteraction {
	@Override
	public void execute(MinecraftServer server, ServerPlayer player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.displayClientMessage(HTMTexts.NOT_OWNER, false);
			return;
		}

		object.setLock(null);
		player.displayClientMessage(HTMTexts.CONTAINER_UNLOCKED, false);
	}
}
