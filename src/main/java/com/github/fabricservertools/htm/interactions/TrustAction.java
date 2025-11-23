package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import java.util.Collection;

public class TrustAction implements LockInteraction {
	private final Collection<NameAndId> trustPlayers;
	private final boolean untrust;

	public TrustAction(Collection<NameAndId> trustPlayers, boolean untrust) {
		this.trustPlayers = trustPlayers;
		this.untrust = untrust;
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayer player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.displayClientMessage(HTMComponents.NOT_OWNER, false);
			return;
		}

		for (NameAndId trustPlayer : trustPlayers) {
			if (lock.owner().equals(trustPlayer.id())) {
				player.displayClientMessage(HTMComponents.CANNOT_TRUST_SELF, false);
				continue;
			}

            Component playerName = Component.literal(trustPlayer.name()).withStyle(ChatFormatting.WHITE);
			if (untrust) {
				lock.withoutTrusted(trustPlayer.id()).ifPresentOrElse(newLock -> {
					player.displayClientMessage(HTMComponents.UNTRUST.apply(playerName), false);
					object.setLock(newLock);
				}, () -> player.displayClientMessage(HTMComponents.PLAYER_NOT_TRUSTED.apply(playerName), false));
			} else {
				lock.withTrusted(trustPlayer.id()).ifPresentOrElse(newLock -> {
					player.displayClientMessage(HTMComponents.TRUST.apply(playerName), false);
					object.setLock(newLock);
				}, () -> player.displayClientMessage(HTMComponents.ALREADY_TRUSTED.apply(playerName), false));
			}
		}
	}
}
