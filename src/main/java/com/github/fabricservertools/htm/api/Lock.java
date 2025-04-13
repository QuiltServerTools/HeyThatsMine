package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;

public interface Lock {
	boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock);

	default Lock withOwner(ServerPlayerEntity player) {
		return this;
	}

	default void onInfo(ServerPlayerEntity player, HTMContainerLock lock) {}

	Codec<? extends Lock> codec();
}
