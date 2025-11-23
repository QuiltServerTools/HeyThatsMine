package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface LockableObject {

	default void writeLock(ValueOutput view) {
		getLock().ifPresent(lock -> view.store("container_lock", HTMContainerLock.CODEC, lock));
	}

	default void readLock(ValueInput view, Consumer<HTMContainerLock> consumer) {
		view.getString("Type").ifPresentOrElse(
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

	default boolean canOpenUnchecked(ServerPlayer player) {
		return canOpen(player).orElse(true);
	}

	default Optional<Boolean> canOpen(ServerPlayer player) {
		return getLock().map(lock -> lock.canOpen(player));
	}

	Optional<HTMContainerLock> getLock();

	void setLock(HTMContainerLock lock);
}
