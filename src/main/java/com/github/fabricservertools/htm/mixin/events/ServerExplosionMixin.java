package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.BlockExplodeCallback;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin implements Explosion {

	@WrapOperation(method = "calculateExplodedPositions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ExplosionDamageCalculator;shouldBlockExplode(Lnet/minecraft/world/level/Explosion;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;F)Z"))
	private boolean HTMExplosionProtectionCheck(ExplosionDamageCalculator damageCalculator, Explosion explosion, BlockGetter level, BlockPos pos,
                                                BlockState state, float power, Operation<Boolean> original) {
		return BlockExplodeCallback.EVENT.invoker().explode(damageCalculator, this, pos, state) == InteractionResult.PASS
				&& original.call(damageCalculator, explosion, level, pos, state, power);
	}
}
