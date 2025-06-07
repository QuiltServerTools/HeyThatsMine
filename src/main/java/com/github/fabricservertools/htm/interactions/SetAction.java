package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockType;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SetAction implements LockInteraction {
	private final Lock setType;

	public SetAction(Lock setType) {
		this.setType = setType;
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayerEntity player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (lock != null && !lock.isOwner(player)) {
			player.sendMessage(Text.translatable("text.htm.error.not_owner"), false);
			return;
		}

		HTMContainerLock newLock = lock != null ? lock.withType(setType) : new HTMContainerLock(setType, player);
		object.setLock(newLock);
		player.sendMessage(Text.translatable("text.htm.set", LockType.id(setType).toUpperCase()), false);
	}

	@Override
	public boolean requiresLock() {
		return false;
	}
}
