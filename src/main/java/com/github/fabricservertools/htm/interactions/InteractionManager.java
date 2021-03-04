package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableChestBlock;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;

public class InteractionManager {
	public static HashMap<ServerPlayerEntity, LockInteraction> pendingActions = new HashMap<>();
	public static HashSet<ServerPlayerEntity> persisting = new HashSet<>();

	public static void execute(ServerPlayerEntity player, World world, BlockPos pos) {
		LockInteraction action = pendingActions.get(player);
		action.execute(player, world, pos, getLock(player, pos));

		if (!persisting.contains(player)) {
			pendingActions.remove(player);
		}
	}

	public static HTMContainerLock getLock(ServerPlayerEntity player, BlockPos pos) {
		HTMContainerLock lock = getLock(player.getServerWorld(), pos);
		if (lock == null) {
			player.sendMessage(new TranslatableText("text.htm.error.unlockable"), false);
		}

		return lock;
	}

	public static HTMContainerLock getLock(ServerWorld world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity == null) return null;

		return getLock(world, blockEntity);
	}

	public static HTMContainerLock getLock(ServerWorld world, BlockEntity blockEntity) {
		BlockState state = blockEntity.getCachedState();

		if (!(blockEntity instanceof LockableContainerBlockEntity)) {
			return null;
		}

		HTMContainerLock lock = ((LockableObject) blockEntity).getLock();
		if (state.getBlock() instanceof LockableChestBlock) {
			lock = ((LockableChestBlock) state.getBlock()).getLockAt(state, world, blockEntity.getPos());
		}

		return lock;
	}

	public static void togglePersist(ServerPlayerEntity player) {
		if (persisting.contains(player)) {
			persisting.remove(player);
		} else {
			persisting.add(player);
		}
	}
}
