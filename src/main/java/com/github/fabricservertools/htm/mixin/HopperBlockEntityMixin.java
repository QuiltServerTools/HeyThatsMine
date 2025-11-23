package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
	@Inject(method = "suckInItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;)Z", at = @At(value = "FIELD", target = "Lnet/minecraft/core/Direction;DOWN:Lnet/minecraft/core/Direction;", shift = At.Shift.AFTER), cancellable = true)
    private static void extractHTMCheck(Level world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        // only checks extraction, so only needs to check block above hopper
        if (!isBlockContainerHopperable(world, BlockPos.containing(hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ()).relative(Direction.UP)))
            cir.setReturnValue(true); // if block is not hopperable, cancel the extract method call
        // otherwise continue the extract method as normal
    }

	//TODO optimize better
    @Unique
    private static boolean isBlockContainerHopperable(Level world, BlockPos pos) {
        if (world.isClientSide()) {
            return true;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) { // no block entity above
            return true;
        }

        BlockState state = world.getBlockState(pos);
        Optional<HTMContainerLock> lock = InteractionManager.getLock((ServerLevel) world, pos, blockEntity);
        return lock.map(l -> l.flag(FlagType.HOPPERS, state)).orElse(true);
    }
}
