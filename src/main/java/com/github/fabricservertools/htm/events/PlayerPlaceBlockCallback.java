package com.github.fabricservertools.htm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;

/**
 * Callback for player placing a block
 * Called before block is placed
 */
public interface PlayerPlaceBlockCallback {
	Event<PlayerPlaceBlockCallback> EVENT = EventFactory.createArrayBacked(PlayerPlaceBlockCallback.class,
			(listeners) -> (player, context) -> {
				for (PlayerPlaceBlockCallback listener : listeners) {
					ActionResult result = listener.place(player, context);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			});

	ActionResult place(PlayerEntity player, ItemPlacementContext context);
}
