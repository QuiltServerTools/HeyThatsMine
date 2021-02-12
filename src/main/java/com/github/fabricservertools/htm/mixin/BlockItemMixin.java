package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.HTMRegistry;
import com.github.fabricservertools.htm.InteractionManager;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemPlacementContext;getBlockPos()Lnet/minecraft/util/math/BlockPos;"))
    private void place(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        try {
            BlockPos pos = context.getBlockPos();
            World world = context.getWorld();
            PlayerEntity playerEntity = context.getPlayer();
            BlockState state = world.getBlockState(pos);

            if (world.isClient) return;
            if (!state.getBlock().hasBlockEntity()) return;

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LockableObject) {
                if(HTM.config.autolockingContainers.contains(Registry.BLOCK.getId(state.getBlock()))) {
                    if (InteractionManager.getLock((ServerWorld) world, blockEntity).isLocked()) return;

                    HTMContainerLock lock = ((LockableObject) blockEntity).getLock();

                    lock.setType(HTMRegistry.getLockFromName("private").getDeclaredConstructor().newInstance(), (ServerPlayerEntity) playerEntity);
                    playerEntity.sendMessage(new TranslatableText("text.htm.set", "PRIVATE"), false);
                }
            }
        } catch (Exception e) {
            HTM.LOGGER.warn("Something went wrong auto locking");
            e.printStackTrace();
        }
    }
}
