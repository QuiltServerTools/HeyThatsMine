package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
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
	public void execute(ServerPlayerEntity player, World world, BlockPos pos, HTMContainerLock lock) {
		if (!lock.isLocked()) {
			player.sendMessage(Text.translatable("text.htm.error.no_lock"), false);
			return;
		}

		if (!lock.isOwner(player)) {
			player.sendMessage(Text.translatable("text.htm.error.not_owner"), false);
			return;
		}

		if (lock.getOwner() == transferPlayer.getId()) {
			player.sendMessage(Text.translatable("text.htm.error.trust_self"), false);
			return;
		}

		lock.transfer(transferPlayer.getId());
		player.sendMessage(Text.translatable("text.htm.transfer", transferPlayer.getName()), false);
	}
}
