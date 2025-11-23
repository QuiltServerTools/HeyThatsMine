package com.github.fabricservertools.htm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockExplodeCallback {
	Event<BlockExplodeCallback> EVENT = EventFactory.createArrayBacked(BlockExplodeCallback.class,
			(listeners) -> (explosionBehavior, explosion, pos, state) -> {
				for (BlockExplodeCallback listener : listeners) {
					InteractionResult result = listener.explode(explosionBehavior, explosion, pos, state);

					if (result != InteractionResult.PASS) {
						return result;
					}
				}

				return InteractionResult.PASS;
			});

	InteractionResult explode(ExplosionDamageCalculator damageCalculator, Explosion explosion, BlockPos pos, BlockState state);
}
