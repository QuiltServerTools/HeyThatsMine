package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.EnderDragonBreakBlockCallback;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderDragon.class)
public abstract class EnderDragonEntityMixin {

	@WrapOperation(method = "checkWalls", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
	public boolean checkEvent(ServerLevel world, BlockPos blockPos, boolean move, Operation<Boolean> original) {
		if (EnderDragonBreakBlockCallback.EVENT.invoker().blockBreak(world, blockPos, move) != InteractionResult.PASS) {
			return false;
		}
		return original.call(world, blockPos, move);
	}
}
