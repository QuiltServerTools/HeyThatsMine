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
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxBlock.class)
public abstract class JukeboxBlockMixin extends BaseEntityBlock {

    protected JukeboxBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "useItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true)
    public void checkLock(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer && !InteractionManager.canOpen(serverPlayer, pos)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "useWithoutItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/JukeboxBlockEntity;popOutTheItem()V"),
            cancellable = true)
    public void checkLock(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer && !InteractionManager.canOpen(serverPlayer, pos)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
