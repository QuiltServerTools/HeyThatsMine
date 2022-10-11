package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    /*
	@Redirect(method = "getInventoryAt(Lnet/minecraft/world/World;DDD)Lnet/minecraft/inventory/Inventory;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"))
	private static BlockEntity getProtectedInventory(World world, BlockPos pos) {
		BlockEntity blockEntity = Objects.requireNonNull(world.getBlockEntity(pos));
		if (world.isClient) return blockEntity;

		HTMContainerLock lock = InteractionManager.getLock((ServerWorld) world, blockEntity);
		if (lock == null) {
			return blockEntity;
		}

		if (!lock.isLocked()) {
			return blockEntity;
		}

		if (lock.getFlags().get("hoppers")) {
			return blockEntity;
		}

		return null;
	}
    */

    @Inject(method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At("HEAD"), cancellable = true)
    private static void extractHTMCheck(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        // only checks extraction, so only needs to check block above hopper
        if (!isBlockContainerHopperable(world, new BlockPos(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ()).offset(Direction.UP)))
            cir.setReturnValue(true); // if block is not hopperable, cancel the extract method call
        // otherwise continue the extract method as normal
    }

    private static boolean isBlockContainerHopperable(World world, BlockPos pos) {
        if (world.isClient) return true;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) // no block entity above
            return true;

        HTMContainerLock lock = InteractionManager.getLock((ServerWorld) world, blockEntity);
        return lock == null || !lock.isLocked() || lock.getFlags().get("hoppers");
    }
}
