package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class SetAction implements LockInteraction {
	private final Lock setType;

	public SetAction(Lock setType) {
		this.setType = setType;
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayer player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (lock != null && !lock.isOwner(player)) {
			player.displayClientMessage(HTMComponents.NOT_OWNER, false);
			return;
		}

		HTMContainerLock newLock = lock != null ? lock.withData(setType) : new HTMContainerLock(setType, player);
		object.setLock(newLock);
		player.displayClientMessage(HTMComponents.CONTAINER_SET.apply(setType.displayName()), false);
	}

	@Override
	public boolean requiresLock() {
		return false;
	}
}
