package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;

public class TransferAction implements LockInteraction {
	private final NameAndId transferPlayer;

	public TransferAction(NameAndId transferPlayer) {
		this.transferPlayer = transferPlayer;
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayer player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.displayClientMessage(HTMTexts.NOT_OWNER, false);
			return;
		}

		if (lock.owner().equals(transferPlayer.id())) {
			player.displayClientMessage(HTMTexts.CANNOT_TRUST_SELF, false);
			return;
		}

		object.setLock(lock.transfer(transferPlayer.id()));
		player.displayClientMessage(HTMTexts.CONTAINER_TRANSFER.apply(Component.literal(transferPlayer.name()).withStyle(ChatFormatting.WHITE)), false);
	}
}
