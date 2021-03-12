package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.WorldBreakBlockCallback;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {
	@Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true)
	private void checkBlockBreakForLock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
		ActionResult result = WorldBreakBlockCallback.EVENT.invoker().blockBreak((World) (Object) this, pos, drop, breakingEntity);

		if (result != ActionResult.PASS) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}
