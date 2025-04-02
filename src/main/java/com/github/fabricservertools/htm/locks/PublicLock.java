package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.Lock;
import com.mojang.serialization.Codec;
import net.minecraft.server.network.ServerPlayerEntity;

public class PublicLock implements Lock {
	public static final Codec<PublicLock> CODEC = Codec.unit(PublicLock::new);

	@Override
	public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
		return true;
	}

	@Override
	public Codec<PublicLock> codec() {
		return CODEC;
	}
}
