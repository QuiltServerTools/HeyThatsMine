package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

public class PublicLock implements LockType {
	@Override
	public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
		return true;
	}

	@Override
	public void onLockSet(ServerPlayerEntity player, HTMContainerLock lock) {
	}

	@Override
	public void onInfo(ServerPlayerEntity player, HTMContainerLock lock) {

	}

	@Override
	public CompoundTag toTag() {
		return new CompoundTag();
	}

	@Override
	public void fromTag(CompoundTag tag) {

	}

}
