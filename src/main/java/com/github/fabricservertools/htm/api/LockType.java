package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.locks.KeyLock;
import com.github.fabricservertools.htm.locks.PrivateLock;
import com.github.fabricservertools.htm.locks.PublicLock;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LockType<T extends Lock> {
	private static final BiMap<String, Codec<? extends Lock>> TYPES = HashBiMap.create();
	private static final Map<String, Lock> INSTANCES = new HashMap<>();

	public static final MapCodec<Lock> CODEC = Codec.STRING.dispatchMap("Type", LockType::id, type -> TYPES.get(type).fieldOf("TypeData"));

	public static String id(Lock lock) {
		return TYPES.inverse().get(lock.codec());
	}

	public static Collection<String> types() {
		return TYPES.keySet();
	}

	public static Lock lock(String id, ServerPlayerEntity owner) {
		Lock lock = INSTANCES.get(id);
		if (lock == null) {
			return null;
		}
		return lock.withOwner(owner);
	}

	private static void register(String id, Codec<? extends Lock> codec, Lock instance) {
		TYPES.put(id, codec);
		INSTANCES.put(id, instance);
	}

	public static void init() {
		register("private", PrivateLock.CODEC, new PrivateLock());
		register("public", PublicLock.CODEC, new PublicLock());
		register("key", KeyLock.CODEC, new KeyLock(null));
	}
}
