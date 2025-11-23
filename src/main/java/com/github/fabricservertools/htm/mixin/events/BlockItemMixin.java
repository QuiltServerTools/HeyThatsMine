package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.PlayerPlaceBlockCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {
	public BlockItemMixin(Properties settings) {
		super(settings);
	}

	/**
	 * Log item placement hook
	 */
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/context/BlockPlaceContext;getClickedPos()Lnet/minecraft/core/BlockPos;"
			),
			method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
			cancellable = true
	)
	public void HTMPlaceEventTrigger(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> info) {
		if (context.getPlayer() == null) return;

		InteractionResult result = PlayerPlaceBlockCallback.EVENT.invoker().place(context.getPlayer(), context);

		if (result != InteractionResult.PASS) {
			info.setReturnValue(InteractionResult.FAIL);
		}
	}
}