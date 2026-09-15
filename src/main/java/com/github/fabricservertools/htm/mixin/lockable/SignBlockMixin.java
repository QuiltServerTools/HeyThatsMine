package com.github.fabricservertools.htm.mixin.lockable;

import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignBlock.class)
public abstract class SignBlockMixin extends BaseEntityBlock implements SimpleWaterloggedBlock {

    protected SignBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "useItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SignBlock;otherPlayerIsEditingSign(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/entity/SignBlockEntity;)Z"),
            cancellable = true)
    public void checkLock(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer && !InteractionManager.canOpen(serverPlayer, pos)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "useWithoutItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SignBlock;otherPlayerIsEditingSign(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/entity/SignBlockEntity;)Z"),
            cancellable = true)
    public void checkLock(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer && !InteractionManager.canOpen(serverPlayer, pos)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
