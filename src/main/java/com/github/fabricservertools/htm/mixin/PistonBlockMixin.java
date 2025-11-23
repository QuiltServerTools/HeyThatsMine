package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBlockMixin {

	@Inject(
			method = "moveBlocks",
			at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"),
			cancellable = true)
	private void HTMPistonMoveCheck(Level world, BlockPos pos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir, @Local BlockState state, @Local(ordinal = 2) BlockPos blockPos) {
		if (world.isClientSide()) {
            return;
        }

		if (state.hasBlockEntity()) {
			Optional<HTMContainerLock> lock = InteractionManager.getLock((ServerLevel) world, blockPos);
			if (lock.isPresent()) {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(
			method = "moveBlocks",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasBlockEntity()Z"),
			cancellable = true)
	private void HTMPistonDestroyCheck(Level world, BlockPos pos, Direction dir, boolean retract, CallbackInfoReturnable<Boolean> cir,
									   @Local BlockState state, @Local(ordinal = 2) BlockPos blockPos) {
		if (world.isClientSide()) {
            return;
        }

		if (state.hasBlockEntity()) {
			Optional<HTMContainerLock> lock = InteractionManager.getLock((ServerLevel) world, blockPos);
			if (lock.isPresent()) {
				cir.setReturnValue(false);
			}
		}
	}
}
