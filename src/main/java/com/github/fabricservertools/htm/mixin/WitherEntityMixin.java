package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.InteractionManager;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WitherEntity.class)
public abstract class WitherEntityMixin {
    @Redirect(method = "mobTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z"))
    private boolean mobTick(World world, BlockPos pos, boolean drop, Entity breakingEntity) {
        if (world.isClient) return false;

        BlockState state = world.getBlockState(pos);
        if (!state.getBlock().hasBlockEntity()) return world.breakBlock(pos, true, breakingEntity);;

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof LockableObject) {
            if (InteractionManager.getLock((ServerWorld) world, pos).isLocked()) return false;
        }

        return world.breakBlock(pos, true, breakingEntity);
    }
}
