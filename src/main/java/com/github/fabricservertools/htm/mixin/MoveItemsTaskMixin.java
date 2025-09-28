package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MoveItemsTask;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(MoveItemsTask.class)
public abstract class MoveItemsTaskMixin extends MultiTickTask<PathAwareEntity> {

    public MoveItemsTaskMixin(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState) {
        super(requiredMemoryState);
    }

    @Inject(method = "isLocked", at = @At("RETURN"), cancellable = true)
    private void checkLockedContainerFlags(MoveItemsTask.Storage storage, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && storage.blockEntity().getWorld() instanceof ServerWorld world) {
            Optional<HTMContainerLock> lock = InteractionManager.getLock(world, storage.pos(), storage.blockEntity());
            cir.setReturnValue(lock.isPresent() && !lock.get().flag(FlagType.COPPER_GOLEMS));
        }
    }
}
