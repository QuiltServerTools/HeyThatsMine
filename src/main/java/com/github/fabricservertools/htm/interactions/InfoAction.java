package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockType;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.stream.Collectors;

public class InfoAction implements LockInteraction {

	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		Optional<PlayerConfigEntry> owner = server.getApiServices().nameToIdCache().getByUuid(lock.owner());

		if (owner.isEmpty()) {
            HTM.LOGGER.error("Can't find lock owner: {}", lock.owner());
			return;
		}

		player.sendMessage(HTMTexts.DIVIDER, false);
		player.sendMessage(HTMTexts.CONTAINER_LOCK_TYPE.apply(LockType.id(lock.type()).toUpperCase()), false);
		player.sendMessage(HTMTexts.CONTAINER_OWNER.apply(owner.get().name()), false);
		if (lock.isOwner(player)) {
			String trustedList = lock.trusted()
					.stream()
					.map(uuid -> Utility.getNameFromUUID(uuid, server))
					.collect(Collectors.joining(", "));

			player.sendMessage(HTMTexts.CONTAINER_TRUSTED.apply(trustedList), false);
			lock.type().onInfo(player, lock);
		}
		player.sendMessage(HTMTexts.DIVIDER, false);
	}
}
