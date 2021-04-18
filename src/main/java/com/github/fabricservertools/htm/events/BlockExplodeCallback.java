package com.github.fabricservertools.htm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public interface BlockExplodeCallback {
	Event<BlockExplodeCallback> EVENT = EventFactory.createArrayBacked(BlockExplodeCallback.class,
			(listeners) -> (explosionBehavior, explosion, world, pos, state, power) -> {
				for (BlockExplodeCallback listener : listeners) {
					ActionResult result = listener.explode(explosionBehavior, explosion, world, pos, state, power);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			});

	ActionResult explode(ExplosionBehavior explosionBehavior, Explosion explosion, World world, BlockPos pos, BlockState state, float power);
}
