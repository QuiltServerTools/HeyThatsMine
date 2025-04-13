package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RemoveAction implements LockInteraction {
	@Override
	public void execute(ServerPlayerEntity player, World world, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.sendMessage(Text.translatable("text.htm.error.not_owner"), false);
			return;
		}

		object.setLock(null);
		player.sendMessage(Text.translatable("text.htm.unlocked"), false);
	}
}
