package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.Lock;
import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;

public class PublicLock implements Lock {
	public static final PublicLock INSTANCE = new PublicLock();
	public static final Codec<PublicLock> CODEC = Codec.unit(INSTANCE);

	private PublicLock() {}

	@Override
	public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
		return true;
	}

	@Override
	public Type type() {
		return Type.PUBLIC;
	}
}
