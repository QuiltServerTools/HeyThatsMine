package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.api.LockableObject;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin {
	@Redirect(method = "destroyBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
	private boolean mobTick(World world, BlockPos pos, boolean move) {
		if (world.isClient) return false;

		BlockState state = world.getBlockState(pos);
		if (!state.getBlock().hasBlockEntity()) return world.removeBlock(pos, move);
		;

		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (blockEntity instanceof LockableObject) {
			if (InteractionManager.getLock((ServerWorld) world, pos).isLocked()) return false;
		}

		return world.removeBlock(pos, move);
	}
}
