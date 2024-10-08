package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.EnderDragonBreakBlockCallback;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin {

	@WrapOperation(method = "destroyBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
	public boolean checkEvent(ServerWorld world, BlockPos blockPos, boolean move, Operation<Boolean> original) {
		if (EnderDragonBreakBlockCallback.EVENT.invoker().blockBreak(world, blockPos, move) != ActionResult.PASS) {
			return false;
		}
		return original.call(world, blockPos, move);
	}
}
