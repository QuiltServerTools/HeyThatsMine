package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Player;
import java.util.UUID;

public class Utility {

    public static String getNameFromUUID(UUID uuid, MinecraftServer server) {
        return server.services().nameToIdCache().get(uuid)
                .map(NameAndId::name)
                .orElse("unknown");
    }

	public static Component getFormattedNameFromUUID(UUID uuid, MinecraftServer server) {
        return Component.literal(getNameFromUUID(uuid, server)).withStyle(ChatFormatting.WHITE);
	}

	public static GlobalTrustState getGlobalTrustState(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(GlobalTrustState.TYPE);
	}

	public static void sendMessage(Player player, Component message) {
		sendMessage(player, message, false);
	}

	public static void sendMessage(Player player, Component message, boolean actionBar) {
		if (!InteractionManager.noMessage.contains(player.getUUID())) {
			player.displayClientMessage(message, actionBar);
		}
	}
}
