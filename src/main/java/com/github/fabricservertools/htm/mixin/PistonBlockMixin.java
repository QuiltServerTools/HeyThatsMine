package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin {

	@Inject(
			method = "move",
			at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"),
			cancellable = true)
	private void HTMPistonMoveCheck(World world, BlockPos pos, Direction dir, boolean extend, CallbackInfoReturnable<Boolean> cir, @Local BlockState state, @Local(ordinal = 2) BlockPos blockPos) {
		if (world.isClient()) {
            return;
        }

		if (state.hasBlockEntity()) {
			Optional<HTMContainerLock> lock = InteractionManager.getLock((ServerWorld) world, blockPos);
			if (lock.isPresent()) {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(
			method = "move",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;hasBlockEntity()Z"),
			cancellable = true)
	private void HTMPistonDestroyCheck(World world, BlockPos pos, Direction dir, boolean retract, CallbackInfoReturnable<Boolean> cir,
									   @Local BlockState state, @Local(ordinal = 2) BlockPos blockPos) {
		if (world.isClient()) {
            return;
        }

		if (state.hasBlockEntity()) {
			Optional<HTMContainerLock> lock = InteractionManager.getLock((ServerWorld) world, blockPos);
			if (lock.isPresent()) {
				cir.setReturnValue(false);
			}
		}
	}
}
