package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import com.mojang.authlib.GameProfile;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.PersistentState;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utility {
	public static String getNameFromUUID (UUID uuid, MinecraftServer server) {
		Optional<GameProfile> ownerProfile = server.getUserCache().getByUuid(uuid);

		return ownerProfile.isPresent() ? ownerProfile.get().getName() : "unknown";
	}

	public static GlobalTrustState getGlobalTrustState(MinecraftServer server) {
		return server.getOverworld().getPersistentStateManager().getOrCreate(
                new PersistentState.Type<>(GlobalTrustState::new, GlobalTrustState::fromNbt, DataFixTypes.LEVEL),
				"globalTrust");
	}

	public static void sendMessage(PlayerEntity player, Text message) {
		sendMessage(player, message, false);
	}

	public static void sendMessage(PlayerEntity player, Text message, boolean actionBar) {
		if (!InteractionManager.noMessage.contains(player.getUuid())) {
			player.sendMessage(message, actionBar);
		}
	}

	/**
	 * Accepts a collection of user ids and renders them as a list of usernames.
	 * @param userIds The unique collection of user ids.
	 * @param minecraftServer The Minecraft server to query for names.
	 * @return A comma-delimited string containing a list of names.
	 */
	public static String joinPlayerNames(
			HashSet<UUID> userIds,
			MinecraftServer minecraftServer
	) {
		return Utility.joinPlayerNames(userIds, minecraftServer, ", ");
	}

	/**
	 * Accepts a collection of user ids and renders them as a list of usernames.
	 * @param userIds The unique collection of user ids.
	 * @param minecraftServer The Minecraft server to query for names.
	 * @param delimiter The delimiter to use when separating names.
	 * @return A string containing a list of names separated by {@code delimiter}.
	 */
	public static String joinPlayerNames(
			HashSet<UUID> userIds,
			MinecraftServer minecraftServer,
			String delimiter
	) {
		return userIds
				.stream()
				.map(uuid -> Utility.getNameFromUUID(uuid, minecraftServer))
				.collect(Collectors.joining(delimiter));
	}
}
