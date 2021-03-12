package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class Utility {
	public static String getNameFromUUID (UUID uuid, MinecraftServer server) {
		GameProfile ownerProfile = server.getUserCache().getByUuid(uuid);

		return ownerProfile != null ? ownerProfile.getName() : "unknown";
	}

	public static GlobalTrustState getGlobalTrustState(MinecraftServer server) {
		return server.getOverworld().getPersistentStateManager().getOrCreate(GlobalTrustState::new, "globalTrust");
	}
}
