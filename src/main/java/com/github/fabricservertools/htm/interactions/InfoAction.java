package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockType;
import com.github.fabricservertools.htm.api.LockableObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.stream.Collectors;

public class InfoAction implements LockInteraction {

	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		Optional<GameProfile> owner = Optional.ofNullable(server.getUserCache()).flatMap(cache -> cache.getByUuid(lock.owner()));

		if (owner.isEmpty()) {
            HTM.LOGGER.error("Can't find lock owner: {}", lock.owner());
			return;
		}

		player.sendMessage(Text.translatable("text.htm.divider"), false);
		player.sendMessage(Text.translatable("text.htm.type", LockType.id(lock.type()).toUpperCase()), false);
		player.sendMessage(Text.translatable("text.htm.owner", owner.get().getName()), false);
		if (lock.isOwner(player)) {
			String trustedList = lock.trusted()
					.stream()
					.map(uuid -> Utility.getNameFromUUID(uuid, server))
					.collect(Collectors.joining(", "));

			player.sendMessage(Text.translatable("text.htm.trusted", trustedList), false);
			lock.type().onInfo(player, lock);
		}
		player.sendMessage(Text.translatable("text.htm.divider"), false);
	}
}
