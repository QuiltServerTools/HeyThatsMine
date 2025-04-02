package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TransferAction implements LockInteraction {
	private final GameProfile transferPlayer;

	public TransferAction(GameProfile transferPlayer) {
		this.transferPlayer = transferPlayer;
	}

	@Override
	public void execute(ServerPlayerEntity player, World world, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.sendMessage(Text.translatable("text.htm.error.not_owner"), false);
			return;
		}

		if (lock.owner().equals(transferPlayer.getId())) {
			player.sendMessage(Text.translatable("text.htm.error.trust_self"), false);
			return;
		}

		object.setLock(lock.transfer(transferPlayer.getId()));
		player.sendMessage(Text.translatable("text.htm.transfer", transferPlayer.getName()), false);
	}
}
