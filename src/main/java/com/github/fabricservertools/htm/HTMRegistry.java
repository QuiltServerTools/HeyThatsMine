package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.api.LockType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.Nullable;

public final class HTMRegistry {
    private static BiMap<String, LockType> lockTypes = HashBiMap.create();
    private static BiMap<String, FlagType> flagTypes = HashBiMap.create();

    public static void registerLockType(String name, LockType lockType) {
        lockTypes.put(name.toLowerCase(), lockType);
    }

    public static void registerFlagType(String name, FlagType flagType) {
        flagTypes.put(name.toLowerCase(), flagType);
    }

    public static BiMap<String, LockType> getLockTypes() {
        return lockTypes;
    }

    public static BiMap<String, FlagType> getFlagTypes() {
        return flagTypes;
    }

    @Nullable
    public static String getNameFromLock(LockType lockType) {
        return lockTypes.inverse().get(lockType);
    }

    @Nullable
    public static String getNameFromFlag(FlagType flagType) {
        return flagTypes.inverse().get(flagType);
    }

    @Nullable
    public static LockType getLockFromName(String name) {
        return lockTypes.get(name);
    }

    @Nullable
    public static FlagType getFlagFromName(String string) {
        return flagTypes.get(string);
    }
}
