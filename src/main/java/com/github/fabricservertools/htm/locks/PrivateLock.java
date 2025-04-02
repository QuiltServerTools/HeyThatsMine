package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.Lock;
import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;

public class PrivateLock implements Lock {
	public static final Codec<PrivateLock> CODEC = Codec.unit(PrivateLock::new);

	@Override
	public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
		if (lock.isTrusted(player.getUuid())) return true;
		return Utility.getGlobalTrustState(player.server).isTrusted(lock.owner(), player.getUuid());
	}

	@Override
	public Codec<PrivateLock> codec() {
		return CODEC;
	}
}
