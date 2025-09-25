package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.locks.KeyLock;
import com.github.fabricservertools.htm.locks.PrivateLock;
import com.github.fabricservertools.htm.locks.PublicLock;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// TODO to enum?
public final class LockType {
	private static final BiMap<String, Codec<? extends Lock>> TYPES = HashBiMap.create();
	private static final Map<String, Function<ServerPlayerEntity, Lock>> FACTORIES = new HashMap<>();

	// A codec that dispatches a lock type (string) from the "Type" key, and then uses the lock type's codec to create a map codec that has a "TypeData" key as field
	public static final MapCodec<Lock> CODEC = Codec.STRING.dispatchMap("Type", LockType::id, type -> TYPES.get(type).fieldOf("TypeData"));

	private LockType() {}

	public static String id(Lock lock) {
		return TYPES.inverse().get(lock.codec());
	}

    public static Text name(Lock lock) {
        return Text.literal(id(lock).toUpperCase()).formatted(Formatting.WHITE);
    }

	public static Collection<String> types() {
		return TYPES.keySet();
	}

	public static Lock lock(String id, ServerPlayerEntity owner) {
		Function<ServerPlayerEntity, Lock> lock = FACTORIES.get(id);
		if (lock == null) {
			return null;
		}
		return lock.apply(owner);
	}

	private static void register(String id, Codec<? extends Lock> codec, Lock instance) {
		TYPES.put(id, codec);
		FACTORIES.put(id, player -> instance);
	}

	private static void register(String id, Codec<? extends Lock> codec, Function<ServerPlayerEntity, Lock> factory) {
		TYPES.put(id, codec);
		FACTORIES.put(id, factory);
	}

	public static void init() {
		register("private", PrivateLock.CODEC, PrivateLock.INSTANCE);
		register("public", PublicLock.CODEC, PublicLock.INSTANCE);
		register("key", KeyLock.CODEC, KeyLock::fromMainHandItem);
	}
}
