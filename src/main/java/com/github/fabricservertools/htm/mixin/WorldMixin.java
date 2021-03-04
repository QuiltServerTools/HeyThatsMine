package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {
	@Inject(method = "breakBlock", at = @At("HEAD"), cancellable = true)
	private void checkBlockBreakForLock(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
		World world = (World) (Object) this;
		if (world.isClient) return;

		HTMContainerLock lock = InteractionManager.getLock((ServerWorld) world, pos);

		if (lock == null) return;
		if(!lock.isLocked()) return;

		if (breakingEntity instanceof ServerPlayerEntity) {
			if (lock.isOwner((ServerPlayerEntity) breakingEntity)) return;
		}

		cir.setReturnValue(false);
		cir.cancel();
	}
}
