package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;

import java.util.Optional;
import java.util.stream.Collectors;

public class InfoAction implements LockInteraction {

	@Override
	public void execute(MinecraftServer server, ServerPlayer player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		Optional<NameAndId> owner = server.services().nameToIdCache().get(lock.owner());

		if (owner.isEmpty()) {
            HTM.LOGGER.error("Can't find lock owner: {}", lock.owner());
			return;
		}

		player.displayClientMessage(HTMComponents.DIVIDER, false);
		player.displayClientMessage(HTMComponents.CONTAINER_LOCK_TYPE.apply(lock.lockData().displayName()), false);
		player.displayClientMessage(HTMComponents.CONTAINER_OWNER.apply(Component.literal(owner.get().name()).withStyle(ChatFormatting.WHITE)), false);
		if (lock.isOwner(player)) {
			String trustedList = lock.trusted()
					.stream()
					.map(uuid -> Utility.getNameFromUUID(uuid, server))
					.collect(Collectors.joining(", "));

			player.displayClientMessage(HTMComponents.CONTAINER_TRUSTED.apply(Component.literal(trustedList).withStyle(ChatFormatting.WHITE)), false);
			lock.lockData().onInfo(player, lock);
		}
		player.displayClientMessage(HTMComponents.DIVIDER, false);
	}
}
