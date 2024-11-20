package com.github.fabricservertools.htm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

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

	ActionResult blockBreak(ServerWorld world, BlockPos pos, boolean move);
}
