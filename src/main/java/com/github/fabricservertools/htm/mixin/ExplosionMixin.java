package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.InteractionManager;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;canDestroyBlock(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;F)Z"))
    private boolean checkProtection(ExplosionBehavior explosionBehavior, Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
        boolean original = explosionBehavior.canDestroyBlock((Explosion) (Object) this, world, pos, state, power);

        if (!state.getBlock().hasBlockEntity()) return original;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LockableObject) {
            if (InteractionManager.getLock((ServerWorld) world, pos).isLocked()) return false;
        }

        return original;
    }
}
