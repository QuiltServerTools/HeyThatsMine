package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(PistonBlock.class)
public abstract class PistonBlockMixin {
	@Inject(
			method = "move",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
					ordinal = 1
			),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION,
			cancellable = true)
	private void HTMPistonMoveCheck(World world, BlockPos pos, Direction dir, boolean retract, CallbackInfoReturnable<Boolean> cir, BlockPos blockPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> map, List<BlockPos> list, List<BlockState> list2, int i, BlockPos blockPos2, BlockState blockState) {
		if (world.isClient) return;

		if (blockState.hasBlockEntity()) {
			HTMContainerLock lock = InteractionManager.getLock((ServerWorld) world, blockPos2);
			if (lock != null && lock.isLocked()) {
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(
			method = "move",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
					ordinal = 2
			),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION,
			cancellable = true)
	private void HTMPistonDestroyCheck(World world, BlockPos pos, Direction dir, boolean retract, CallbackInfoReturnable<Boolean> cir, BlockPos blockPos, PistonHandler pistonHandler, Map<BlockPos, BlockState> map, List<BlockPos> list, List<BlockState> list2, List<BlockPos> list3, BlockState[] blockStates, Direction direction, int j, int k, BlockPos blockPos3, BlockState blockState2) {
		if (world.isClient) return;

		if (blockState2.hasBlockEntity()) {
			HTMContainerLock lock = InteractionManager.getLock((ServerWorld) world, blockPos3);
			if (lock != null && lock.isLocked()) {
				cir.setReturnValue(false);
			}
		}
	}
}
