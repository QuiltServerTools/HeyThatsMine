package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.BlockExplodeCallback;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
	@Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;canDestroyBlock(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)Z"))
	private boolean checkProtection(ExplosionBehavior explosionBehavior, Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
		ActionResult result = BlockExplodeCallback.EVENT.invoker().explode(explosionBehavior, explosion, world, pos, state, power);

		if (result == ActionResult.PASS) {
			return explosionBehavior.canDestroyBlock((Explosion) (Object) this, world, pos, state, power);
		}

		return false;
	}
}
