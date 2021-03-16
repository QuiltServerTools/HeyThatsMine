package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTMContainerLock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

public interface Lock {
	boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock);

	void onLockSet(ServerPlayerEntity player, HTMContainerLock lock);

	void onInfo(ServerPlayerEntity player, HTMContainerLock lock);

	CompoundTag toTag();

	void fromTag(CompoundTag tag);

	LockType<?> getType();
}
