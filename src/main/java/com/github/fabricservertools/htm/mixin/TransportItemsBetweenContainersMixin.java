package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

@Mixin(TransportItemsBetweenContainers.class)
public abstract class TransportItemsBetweenContainersMixin extends Behavior<PathfinderMob> {

    public TransportItemsBetweenContainersMixin(Map<MemoryModuleType<?>, MemoryStatus> map) {
        super(map);
    }

    @Inject(method = "isContainerLocked", at = @At("RETURN"), cancellable = true)
    private void checkLockedContainerFlags(TransportItemsBetweenContainers.TransportItemTarget target, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && target.blockEntity().getLevel() instanceof ServerLevel world) {
            Optional<HTMContainerLock> lock = InteractionManager.getLock(world, target.pos(), target.blockEntity());
            cir.setReturnValue(lock.isPresent() && !lock.get().flag(FlagType.COPPER_GOLEMS, target.state()));
        }
    }
}
