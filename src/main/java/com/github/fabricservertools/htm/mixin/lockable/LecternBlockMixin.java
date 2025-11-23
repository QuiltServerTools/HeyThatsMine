package com.github.fabricservertools.htm.mixin.lockable;

import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlock.class)
public abstract class LecternBlockMixin extends BaseEntityBlock {

    protected LecternBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "tryPlaceBook",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LecternBlock;placeBook(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/ItemStack;)V"),
            cancellable = true)
    private static void checkLock(LivingEntity user, Level world, BlockPos pos, BlockState state, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (user instanceof ServerPlayer serverPlayer && !InteractionManager.canOpen(serverPlayer, pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "useWithoutItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LecternBlock;openScreen(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"),
            cancellable = true)
    public void checkLock(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer && !InteractionManager.canOpen(serverPlayer, pos)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
