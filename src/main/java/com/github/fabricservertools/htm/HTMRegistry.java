package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.api.LockType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public final class HTMRegistry {
	private static final BiMap<String, Class<? extends LockType>> lockTypes = HashBiMap.create();
	private static final HashSet<String> flagTypes = new HashSet<>();

	public static void registerLockType(String name, Class<? extends LockType> lockType) {
		lockTypes.put(name.toLowerCase(), lockType);
	}

	public static void registerFlagType(String name) {
		flagTypes.add(name.toLowerCase());
	}

	public static BiMap<String, Class<? extends LockType>> getLockTypes() {
		return lockTypes;
	}

	public static HashSet<String> getFlagTypes() {
		return flagTypes;
	}

	public static String getNameFromLock(LockType lockType) {
		String name = lockTypes.inverse().get(lockType.getClass());
		if (name == null) name = "ERROR";

		return name;
	}

	@Nullable
	public static Class<? extends LockType> getLockFromName(String name) {
		return lockTypes.get(name);
	}
}
