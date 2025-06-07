package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import java.util.Optional;
import java.util.function.Consumer;

public interface LockableObject {

	default void writeLock(WriteView view) {
		getLock().ifPresent(lock -> view.put("container_lock", HTMContainerLock.CODEC, lock));
	}

	default void readLock(ReadView view, Consumer<HTMContainerLock> consumer) {
		view.getOptionalString("Type").ifPresentOrElse(
				legacy -> {
					// Legacy, lock data was stored in the root NBT object before 1.21.5
					// Even Mojang thinks this shouldn't be done anymore and marked the method as deprecated, will probably end up removing this functionality
					// once this method is removed too
					view.read(HTMContainerLock.MAP_CODEC).ifPresentOrElse(
							consumer,
							() -> {
								HTM.LOGGER.warn("Failed to read legacy container lock data!"); // Can't really do much here
								consumer.accept(null);
							}
					);
				},
				() -> view.read("container_lock", HTMContainerLock.CODEC).ifPresentOrElse(consumer, () -> consumer.accept(null))
		);
	}

	default boolean canOpenUnchecked(ServerPlayerEntity player) {
		return canOpen(player).orElse(true);
	}

	default Optional<Boolean> canOpen(ServerPlayerEntity player) {
		return getLock().map(lock -> lock.canOpen(player));
	}

	Optional<HTMContainerLock> getLock();

	void setLock(HTMContainerLock lock);
}
