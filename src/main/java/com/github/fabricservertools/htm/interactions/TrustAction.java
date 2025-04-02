package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
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
	public void execute(ServerPlayerEntity player, World world, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.sendMessage(Text.translatable("text.htm.error.not_owner"), false);
			return;
		}

		for (GameProfile trustPlayer : trustPlayers) {
			if (lock.owner().equals(trustPlayer.getId())) {
				player.sendMessage(Text.translatable("text.htm.error.trust_self"), false);
				continue;
			}

			if (untrust) {
				lock.withoutTrusted(trustPlayer.getId()).ifPresentOrElse(newLock -> {
					player.sendMessage(Text.translatable("text.htm.untrust", trustPlayer.getName()), false);
					object.setLock(newLock);
				}, () -> player.sendMessage(Text.translatable("text.htm.error.not_trusted", trustPlayer.getName()), false));
			} else {
				lock.withTrusted(trustPlayer.getId()).ifPresentOrElse(newLock -> {
					player.sendMessage(Text.translatable("text.htm.trust", trustPlayer.getName()), false);
					object.setLock(newLock);
				}, () -> player.sendMessage(Text.translatable("text.htm.error.already_trusted", trustPlayer.getName()), false));
			}
		}
	}
}
