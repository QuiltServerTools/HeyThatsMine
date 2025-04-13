package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTMContainerLock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface LockInteraction {

	void execute(ServerPlayerEntity player, World world, BlockPos pos, LockableObject object, HTMContainerLock lock);

	default boolean requiresLock() {
		return true;
	}
}
