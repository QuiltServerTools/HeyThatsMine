package com.github.fabricservertools.htm.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;

/**
 * Callback for player placing a block
 * Called before block is placed
 */
public interface PlayerPlaceBlockCallback {
	Event<PlayerPlaceBlockCallback> EVENT = EventFactory.createArrayBacked(PlayerPlaceBlockCallback.class,
			(listeners) -> (player, context) -> {
				for (PlayerPlaceBlockCallback listener : listeners) {
					InteractionResult result = listener.place(player, context);

					if (result != InteractionResult.PASS) {
						return result;
					}
				}

				return InteractionResult.PASS;
			});

	InteractionResult place(Player player, BlockPlaceContext context);
}
