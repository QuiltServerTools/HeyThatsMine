package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.UUID;

public class Utility {
	public static String getNameFromUUID (UUID uuid, MinecraftServer server) {
		Optional<GameProfile> ownerProfile = server.getUserCache().getByUuid(uuid);

		return ownerProfile.isPresent() ? ownerProfile.get().getName() : "unknown";
	}

	public static GlobalTrustState getGlobalTrustState(MinecraftServer server) {
		return server.getOverworld().getPersistentStateManager().getOrCreate(GlobalTrustState.TYPE);
	}

	public static void sendMessage(PlayerEntity player, Text message) {
		sendMessage(player, message, false);
	}

	public static void sendMessage(PlayerEntity player, Text message, boolean actionBar) {
		if (!InteractionManager.noMessage.contains(player.getUuid())) {
			player.sendMessage(message, actionBar);
		}
	}
}
