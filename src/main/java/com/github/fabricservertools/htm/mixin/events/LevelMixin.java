package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.LevelBreakBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
	@Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
	private void checkBlockBreakForLock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
		InteractionResult result = LevelBreakBlockCallback.EVENT.invoker().blockBreak((Level) (Object) this, pos, drop, breakingEntity);

		if (result != InteractionResult.PASS) {
			cir.setReturnValue(false);
			cir.cancel();
		}
	}
}
