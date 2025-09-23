package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public class TrustAction implements LockInteraction {
	private final Collection<PlayerConfigEntry> trustPlayers;
	private final boolean untrust;

	public TrustAction(Collection<PlayerConfigEntry> trustPlayers, boolean untrust) {
		this.trustPlayers = trustPlayers;
		this.untrust = untrust;
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.sendMessage(HTMTexts.NOT_OWNER, false);
			return;
		}

		for (PlayerConfigEntry trustPlayer : trustPlayers) {
			if (lock.owner().equals(trustPlayer.id())) {
				player.sendMessage(HTMTexts.CANNOT_TRUST_SELF, false);
				continue;
			}

			if (untrust) {
				lock.withoutTrusted(trustPlayer.id()).ifPresentOrElse(newLock -> {
					player.sendMessage(HTMTexts.UNTRUST.apply(trustPlayer.name()), false);
					object.setLock(newLock);
				}, () -> player.sendMessage(HTMTexts.PLAYER_NOT_TRUSTED.apply(trustPlayer.name()), false));
			} else {
				lock.withTrusted(trustPlayer.id()).ifPresentOrElse(newLock -> {
					player.sendMessage(HTMTexts.TRUST.apply(trustPlayer.name()), false);
					object.setLock(newLock);
				}, () -> player.sendMessage(HTMTexts.ALREADY_TRUSTED.apply(trustPlayer.name()), false));
			}
		}
	}
}
