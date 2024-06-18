package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public class TrustAction implements LockInteraction {
	private final Collection<GameProfile> trustPlayers;
	private final boolean untrust;

	public TrustAction(Collection<GameProfile> trustPlayers, boolean untrust) {
		this.trustPlayers = trustPlayers;
		this.untrust = untrust;
	}

	@Override
	public void execute(ServerPlayerEntity player, World world, BlockPos pos, HTMContainerLock lock) {
		if (!lock.isLocked()) {
			player.sendMessage(Text.translatable("text.htm.error.no_lock"), false);
			return;
		}

		// Players and managers can manage trustees.
		if (!lock.isOwner(player) && !lock.isManager(player.getUuid())) {
			player.sendMessage(Text.translatable("text.htm.error.not_owner"), false);
			return;
		}

		for (GameProfile trustPlayer : trustPlayers) {
			if (lock.getOwner() == trustPlayer.getId()) {
				player.sendMessage(Text.translatable("text.htm.error.trust_self"), false);
				continue;
			}

			if (untrust) {
				//untrust
				if (lock.removeTrust(trustPlayer.getId())) {
					player.sendMessage(Text.translatable("text.htm.untrust", trustPlayer.getName()), false);
				} else {
					player.sendMessage(Text.translatable("text.htm.error.not_trusted", trustPlayer.getName()), false);
				}
			} else {
				//trust
				if (lock.addTrust(trustPlayer.getId())) {
					player.sendMessage(Text.translatable("text.htm.trust", trustPlayer.getName()), false);
				} else {
					player.sendMessage(Text.translatable("text.htm.error.already_trusted", trustPlayer.getName()), false);
				}
			}
		}
	}
}
