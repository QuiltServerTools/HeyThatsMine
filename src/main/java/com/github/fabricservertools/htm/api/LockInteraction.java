package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface LockInteraction {

	void execute(MinecraftServer server, ServerPlayerEntity player, BlockPos pos, LockableObject object, HTMContainerLock lock);

	default boolean requiresLock() {
		return true;
	}
}
