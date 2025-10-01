package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class TransferAction implements LockInteraction {
	private final PlayerConfigEntry transferPlayer;

	public TransferAction(PlayerConfigEntry transferPlayer) {
		this.transferPlayer = transferPlayer;
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.sendMessage(HTMTexts.NOT_OWNER, false);
			return;
		}

		if (lock.owner().equals(transferPlayer.id())) {
			player.sendMessage(HTMTexts.CANNOT_TRUST_SELF, false);
			return;
		}

		object.setLock(lock.transfer(transferPlayer.id()));
		player.sendMessage(HTMTexts.CONTAINER_TRANSFER.apply(Text.literal(transferPlayer.name()).formatted(Formatting.WHITE)), false);
	}
}
