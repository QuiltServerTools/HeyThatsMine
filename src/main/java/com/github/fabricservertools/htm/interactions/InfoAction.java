package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.HTMRegistry;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.stream.Collectors;

public class InfoAction implements LockInteraction {
	@Override
	public void execute(ServerPlayerEntity player, World world, BlockPos pos, HTMContainerLock lock) {
		if (!lock.isLocked()) {
			player.sendMessage(new TranslatableText("text.htm.error.no_lock"), false);
			return;
		}

		Optional<GameProfile> owner = player.server.getUserCache().getByUuid(lock.getOwner());

		if (owner.isEmpty()) {
			HTM.LOGGER.error("Can't find lock owner: " + lock.getOwner());
			return;
		}

		player.sendMessage(new TranslatableText("text.htm.divider"), false);
		player.sendMessage(new TranslatableText("text.htm.type", HTMRegistry.getLockId(lock.getType().getType()).toUpperCase()), false);
		player.sendMessage(new TranslatableText("text.htm.owner", owner.get().getName()), false);
		if (lock.isOwner(player)) {
			String trustedList = lock.getTrusted()
					.stream()
					.map(uuid -> Utility.getNameFromUUID(uuid, player.server))
					.collect(Collectors.joining(", "));

			player.sendMessage(new TranslatableText("text.htm.trusted", trustedList), false);
			lock.getType().onInfo(player, lock);
		}
		player.sendMessage(new TranslatableText("text.htm.divider"), false);
	}
}
