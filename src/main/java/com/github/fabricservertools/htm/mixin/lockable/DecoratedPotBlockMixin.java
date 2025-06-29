package com.github.fabricservertools.htm.mixin.lockable;

import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotBlock.class)
public abstract class DecoratedPotBlockMixin extends BlockWithEntity implements Waterloggable {

    protected DecoratedPotBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "onUseWithItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/DecoratedPotBlockEntity;getStack()Lnet/minecraft/item/ItemStack;"),
            cancellable = true)
    public void checkLock(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player instanceof ServerPlayerEntity serverPlayer && !InteractionManager.canOpen(serverPlayer, pos)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}
