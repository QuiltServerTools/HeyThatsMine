package com.github.fabricservertools.htm.lock.type;

import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;

public class PublicLock implements Lock {
	public static final PublicLock INSTANCE = new PublicLock();
	public static final Codec<PublicLock> CODEC = Codec.unit(INSTANCE);

	private PublicLock() {}

	@Override
	public boolean canOpen(ServerPlayer player, HTMContainerLock lock) {
		return true;
	}

	@Override
	public Type type() {
		return Type.PUBLIC;
	}
}
