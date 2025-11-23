package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface LockInteraction {

	void execute(MinecraftServer server, ServerPlayer player, BlockPos pos, LockableObject object, HTMContainerLock lock);

	default boolean requiresLock() {
		return true;
	}
}
