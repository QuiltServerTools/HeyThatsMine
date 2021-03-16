package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.api.LockType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

public class PrivateLock implements Lock {
	@Override
	public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
		if (lock.getTrusted().contains(player.getUuid())) return true;
		return Utility.getGlobalTrustState(player.server).isTrusted(lock.getOwner(), player.getUuid());
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

	@Override
	public LockType<?> getType() {
		return LockType.PRIVATE_LOCK;
	}

}
