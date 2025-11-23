package com.github.fabricservertools.htm.listeners;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.events.BlockExplodeCallback;
import com.github.fabricservertools.htm.events.EnderDragonBreakBlockCallback;
import com.github.fabricservertools.htm.events.WorldBreakBlockCallback;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WorldEventListener {
	public static void init() {
		BlockExplodeCallback.EVENT.register(WorldEventListener::onBlockExplode);
		WorldBreakBlockCallback.EVENT.register(WorldEventListener::onBlockBreak);
		EnderDragonBreakBlockCallback.EVENT.register(WorldEventListener::enderDragonBreakBlock);
	}

	private static InteractionResult enderDragonBreakBlock(ServerLevel world, BlockPos pos, boolean b) {
		BlockState state = world.getBlockState(pos);
		if (!state.hasBlockEntity()) return InteractionResult.PASS;

		if (InteractionManager.getLock(world, pos).isPresent()) return InteractionResult.FAIL;

		return InteractionResult.PASS;
	}

	private static InteractionResult onBlockBreak(Level world, BlockPos pos, boolean drop, @Nullable Entity entity) {
		if (world.isClientSide()) {
            return InteractionResult.PASS;
        }

		Optional<HTMContainerLock> lock = InteractionManager.getLock((ServerLevel) world, pos);
		if (lock.isEmpty()) {
            return InteractionResult.PASS;
        }

		if (entity instanceof ServerPlayer) {
			if (lock.get().isOwner((ServerPlayer) entity)) {
                return InteractionResult.PASS;
            }
		}

		return InteractionResult.FAIL;
	}

	private static InteractionResult onBlockExplode(ExplosionDamageCalculator explosionBehavior, Explosion explosion, BlockPos pos, BlockState state) {
		if (!state.hasBlockEntity()) return InteractionResult.PASS;

		if (InteractionManager.getLock(explosion.level(), pos).isPresent()) return InteractionResult.FAIL;

		return InteractionResult.PASS;
	}
}
