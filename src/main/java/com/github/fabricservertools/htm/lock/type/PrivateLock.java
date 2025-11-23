package com.github.fabricservertools.htm.lock.type;

import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;

public class PrivateLock implements Lock {
	public static final PrivateLock INSTANCE = new PrivateLock();
	public static final Codec<PrivateLock> CODEC = Codec.unit(INSTANCE);

	private PrivateLock() {}

	@Override
	public boolean canOpen(ServerPlayer player, HTMContainerLock lock) {
		if (lock.isTrusted(player.getUUID())) return true;
		return Utility.getGlobalTrustState(player.level().getServer()).isTrusted(lock.owner(), player.getUUID());
	}

	@Override
	public Type type() {
		return Type.PRIVATE;
	}
}
