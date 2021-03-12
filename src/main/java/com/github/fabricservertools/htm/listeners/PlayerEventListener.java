package com.github.fabricservertools.htm.listeners;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.HTMRegistry;
import com.github.fabricservertools.htm.api.LockableChestBlock;
import com.github.fabricservertools.htm.api.LockableObject;
import com.github.fabricservertools.htm.events.PlayerPlaceBlockCallback;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Optional;

public class PlayerEventListener {
	public static void init() {
		PlayerPlaceBlockCallback.EVENT.register(PlayerEventListener::onPlace);
		PlayerBlockBreakEvents.BEFORE.register(PlayerEventListener::onBeforeBreak);
		AttackBlockCallback.EVENT.register(PlayerEventListener::onAttackBlock);
	}

	private static ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
		if (world.isClient) return ActionResult.PASS;

		if (InteractionManager.pendingActions.containsKey(player)) {
			InteractionManager.execute((ServerPlayerEntity) player, world, pos);

			world.updateNeighborsAlways(pos, world.getBlockState(pos).getBlock());
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	private static boolean onBeforeBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (world.isClient) return true;
		ServerPlayerEntity playerEntity = (ServerPlayerEntity) player;

		if (blockEntity instanceof LockableContainerBlockEntity) {
			HTMContainerLock lock = InteractionManager.getLock(playerEntity, pos);

			if (!lock.isLocked()) return true;

			if (lock.isOwner((ServerPlayerEntity) player)) {
				if (state.getBlock() instanceof LockableChestBlock) {
					Optional<BlockEntity> unlocked = ((LockableChestBlock) state.getBlock()).getUnlockedPart(state, world, pos);
					if (unlocked.isPresent()) {
						BlockEntity unlockedBlockEntity = unlocked.get();
						((LockableObject) unlockedBlockEntity).setLock(lock);
						return true;
					}
				}


				player.sendMessage(new TranslatableText("text.htm.unlocked"), false);

				return true;
			}

			player.sendMessage(new TranslatableText("text.htm.error.not_owner"), false);
			return false;
		}

		return true;
	}

	private static ActionResult onPlace(PlayerEntity playerEntity, ItemPlacementContext context) {
		try {
			BlockPos pos = context.getBlockPos();
			World world = context.getWorld();
			BlockState state = world.getBlockState(pos);

			if (world.isClient) return ActionResult.PASS;
			if (!state.getBlock().hasBlockEntity()) return ActionResult.PASS;

			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof LockableObject) {
				if (HTM.config.autolockingContainers.contains(Registry.BLOCK.getId(state.getBlock()))) {
					if (InteractionManager.getLock((ServerWorld) world, blockEntity).isLocked()) return ActionResult.PASS;

					HTMContainerLock lock = ((LockableObject) blockEntity).getLock();

					lock.setType(HTMRegistry.getLockFromName("private").getDeclaredConstructor().newInstance(), (ServerPlayerEntity) playerEntity);
					playerEntity.sendMessage(new TranslatableText("text.htm.set", "PRIVATE"), false);
				}
			}
		} catch (Exception e) {
			HTM.LOGGER.warn("Something went wrong auto locking");
			e.printStackTrace();
		}

		return ActionResult.PASS;
	}
}
