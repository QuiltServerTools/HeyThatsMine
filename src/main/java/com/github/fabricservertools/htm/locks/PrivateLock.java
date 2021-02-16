package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.GlobalTrustState;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;

public class PrivateLock implements LockType {
    @Override
    public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
        if (lock.getTrusted().contains(player.getUuid())) return true;
        if (player.getServer().getOverworld().getPersistentStateManager().getOrCreate(
                GlobalTrustState::new, "globalTrust").isTrusted(lock.getOwner(), player.getUuid())) return true;

        return false;
    }

    @Override
    public void onLockSet(ServerPlayerEntity player, HTMContainerLock lock) {
        return;
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
