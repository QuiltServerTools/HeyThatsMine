package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SetAction implements LockInteraction {
	private final Lock setType;

	public SetAction(Lock setType) {
		this.setType = setType;
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (lock != null && !lock.isOwner(player)) {
			player.sendMessage(HTMTexts.NOT_OWNER, false);
			return;
		}

		HTMContainerLock newLock = lock != null ? lock.withType(setType) : new HTMContainerLock(setType, player);
		object.setLock(newLock);
		player.sendMessage(HTMTexts.CONTAINER_SET.apply(setType.displayName()), false);
	}

	@Override
	public boolean requiresLock() {
		return false;
	}
}
