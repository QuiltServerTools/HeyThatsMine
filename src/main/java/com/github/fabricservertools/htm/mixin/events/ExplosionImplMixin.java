package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.BlockExplodeCallback;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExplosionImpl.class)
public abstract class ExplosionImplMixin implements Explosion{

	@WrapOperation(method = "getBlocksToDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;canDestroyBlock(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)Z"))
	private boolean HTMExplosionProtectionCheck(ExplosionBehavior behavior, Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power, Operation<Boolean> original) {
		return BlockExplodeCallback.EVENT.invoker().explode(behavior, this, pos, state) == ActionResult.PASS
				&& original.call(behavior, explosion, world, pos, state, power);
	}
}
