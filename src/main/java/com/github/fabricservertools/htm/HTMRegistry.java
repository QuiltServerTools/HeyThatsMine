package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.api.LockType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;

public final class HTMRegistry {
	private static final BiMap<String, LockType<? extends Lock>> lockTypes = HashBiMap.create();
	private static final HashSet<String> flagTypes = new HashSet<>();

	public static <T extends Lock> LockType<T> registerLockType(String name, LockType<T> lockType) {
		lockTypes.put(name.toLowerCase(), lockType);
		return lockType;
	}

	public static void registerFlagType(String name) {
		flagTypes.add(name.toLowerCase());
	}

	public static BiMap<String, LockType<?>> getLockTypes() {
		return lockTypes;
	}

	public static HashSet<String> getFlagTypes() {
		return flagTypes;
	}

	public static String getLockId(LockType<?> lockType) {
		String id = lockTypes.inverse().get(lockType);
		return id == null ? "ERROR" : id;
	}

	public static @Nullable Optional<Lock> getLock(String name) {
		LockType<?> lockType = lockTypes.get(name);
		return Optional.ofNullable(lockType.build());
	}
}
