package com.github.fabricservertools.htm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface LevelBreakBlockCallback {
	Event<LevelBreakBlockCallback> EVENT = EventFactory.createArrayBacked(LevelBreakBlockCallback.class,
			(listeners) -> (world, pos, drop, breakingEntity) -> {
				for (LevelBreakBlockCallback listener : listeners) {
					InteractionResult result = listener.blockBreak(world, pos, drop, breakingEntity);

					if (result != InteractionResult.PASS) {
						return result;
					}
				}

				return InteractionResult.PASS;
			});

	InteractionResult blockBreak(Level level, BlockPos pos, boolean drop, @Nullable Entity breakingEntity);
}
