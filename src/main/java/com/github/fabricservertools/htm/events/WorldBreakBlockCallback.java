package com.github.fabricservertools.htm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface WorldBreakBlockCallback {
	Event<WorldBreakBlockCallback> EVENT = EventFactory.createArrayBacked(WorldBreakBlockCallback.class,
			(listeners) -> (world, pos, drop, breakingEntity) -> {
				for (WorldBreakBlockCallback listener : listeners) {
					ActionResult result = listener.blockBreak(world, pos, drop, breakingEntity);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			});

	ActionResult blockBreak(World world, BlockPos pos, boolean drop, @Nullable Entity breakingEntity);
}
