package com.github.fabricservertools.htm.mixin.lockable;

import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlock.class)
public abstract class LecternBlockMixin extends BlockWithEntity {

    protected LecternBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "putBookIfAbsent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LecternBlock;putBook(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/item/ItemStack;)V"),
            cancellable = true)
    private static void checkLock(LivingEntity user, World world, BlockPos pos, BlockState state, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (user instanceof ServerPlayerEntity serverPlayer && !InteractionManager.canOpen(serverPlayer, pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onUse",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LecternBlock;openScreen(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)V"),
            cancellable = true)
    public void checkLock(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player instanceof ServerPlayerEntity serverPlayer && !InteractionManager.canOpen(serverPlayer, pos)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
