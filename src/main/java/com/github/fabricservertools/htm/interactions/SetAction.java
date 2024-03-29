package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.HTMRegistry;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.api.LockInteraction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SetAction implements LockInteraction {
	private final Lock setType;

	public SetAction(Lock setType) {
		this.setType = setType;
	}

	@Override
	public void execute(ServerPlayerEntity player, World world, BlockPos pos, HTMContainerLock lock) {
		if (lock.isLocked() && !lock.isOwner(player)) {
			player.sendMessage(Text.translatable("text.htm.error.not_owner"), false);
			return;
		}

		lock.setType(setType, player);
		player.sendMessage(Text.translatable("text.htm.set", HTMRegistry.getLockId(setType.getType()).toUpperCase()), false);
	}
}
