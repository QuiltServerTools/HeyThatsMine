package com.github.fabricservertools.htm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EnderDragonBreakBlockCallback {
	Event<EnderDragonBreakBlockCallback> EVENT = EventFactory.createArrayBacked(EnderDragonBreakBlockCallback.class,
			(listeners) -> (world, pos, move) -> {
				for (EnderDragonBreakBlockCallback listener : listeners) {
					ActionResult result = listener.blockBreak(world, pos, move);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			});

	ActionResult blockBreak(World world, BlockPos pos, boolean move);
}
