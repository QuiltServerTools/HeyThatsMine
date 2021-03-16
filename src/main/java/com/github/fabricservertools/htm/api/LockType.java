package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTMRegistry;
import com.github.fabricservertools.htm.locks.KeyLock;
import com.github.fabricservertools.htm.locks.PrivateLock;
import com.github.fabricservertools.htm.locks.PublicLock;

import java.util.function.Supplier;

public class LockType<T extends Lock> {
	public static LockType<PrivateLock> PRIVATE_LOCK;
	public static LockType<PublicLock> PUBLIC_LOCK;
	public static LockType<KeyLock> KEY_LOCK;

	private final Supplier<T> supplier;

	private static <T extends Lock> LockType<T> register(String id, LockType<T> lockType) {
		return HTMRegistry.registerLockType(id, lockType);
	}

	public LockType(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public Lock build() {
		return supplier.get();
	}

	public static void init() {
		PRIVATE_LOCK = register("private", new LockType<>(PrivateLock::new));
		PUBLIC_LOCK = register("public", new LockType<>(PublicLock::new));
		KEY_LOCK = register("key", new LockType<>(KeyLock::new));
	}
}
