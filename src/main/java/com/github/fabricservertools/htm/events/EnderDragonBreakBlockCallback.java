package com.github.fabricservertools.htm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;

public interface EnderDragonBreakBlockCallback {
	Event<EnderDragonBreakBlockCallback> EVENT = EventFactory.createArrayBacked(EnderDragonBreakBlockCallback.class,
			(listeners) -> (world, pos, move) -> {
				for (EnderDragonBreakBlockCallback listener : listeners) {
					InteractionResult result = listener.blockBreak(world, pos, move);

					if (result != InteractionResult.PASS) {
						return result;
					}
				}

				return InteractionResult.PASS;
			});

	InteractionResult blockBreak(ServerLevel world, BlockPos pos, boolean move);
}
