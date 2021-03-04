package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
	@Redirect(method = "getInventoryAt(Lnet/minecraft/world/World;DDD)Lnet/minecraft/inventory/Inventory;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"))
	private static BlockEntity getProtectedInventory(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (world.isClient) return blockEntity;

		HTMContainerLock lock = InteractionManager.getLock((ServerWorld) world, blockEntity);
		if (!lock.isLocked()) {
			return blockEntity;
		}

		if (lock.getFlags().get("hoppers")) {
			return blockEntity;
		}

		return null;
	}
}
