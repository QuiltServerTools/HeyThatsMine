package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {
	@Shadow
	@Final
	private World world;

	@Shadow
	@Final
	private BlockPos posTo;

	@Inject(
			method = "calculatePush",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
					ordinal = 0
			),
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILEXCEPTION
	)
	private void HTMPistonPushCheck(CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
		if (this.world.isClient) return;

		if (blockState.getBlock().hasBlockEntity()) {
			HTMContainerLock lock = InteractionManager.getLock((ServerWorld) this.world, this.posTo);
			if (lock != null && lock.isLocked()) {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(
			method = "tryMove",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;",
					ordinal = 0
			),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION,
			cancellable = true
	)
	private void HTMPistonMoveCheck(BlockPos pos, Direction arg1, CallbackInfoReturnable<Boolean> cir, BlockState blockState, Block block) {
		if (this.world.isClient) return;

		if (blockState.getBlock().hasBlockEntity()) {
			HTMContainerLock lock = InteractionManager.getLock((ServerWorld) this.world, this.posTo);
			if (lock != null && lock.isLocked()) {
				cir.setReturnValue(true);
			}
		}
	}

	@Inject(
			method = "tryMove",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;",
					ordinal = 1
			),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION,
			cancellable = true
	)
	private void HTMPistonMoveCheck2(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState blockState, Block block, int i, BlockPos blockPos, Block block2) {
		if (this.world.isClient) return;

		if (blockState.getBlock().hasBlockEntity()) {
			HTMContainerLock lock = InteractionManager.getLock((ServerWorld) this.world, blockPos);
			if (lock != null && lock.isLocked()) {
				cir.setReturnValue(false);
			}
		}
	}
}
