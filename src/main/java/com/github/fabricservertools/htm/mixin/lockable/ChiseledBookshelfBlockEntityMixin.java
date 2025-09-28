package com.github.fabricservertools.htm.mixin.lockable;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ChiseledBookshelfBlockEntity.class)
public abstract class ChiseledBookshelfBlockEntityMixin extends BlockEntity implements Inventory, LockableObject {

    @Unique
    private HTMContainerLock lock = null;

    public ChiseledBookshelfBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "readData", at = @At("TAIL"))
    public void readLockData(ReadView view, CallbackInfo ci) {
        readLock(view, lock -> this.lock = lock);
    }

    @Inject(method = "writeData", at = @At("TAIL"))
    public void writeLockData(WriteView view, CallbackInfo ci) {
        writeLock(view);
    }

    @Inject(method = "canPlayerUse", at = @At("HEAD"), cancellable = true)
    public void checkLock(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof ServerPlayerEntity serverPlayer && !canOpenUnchecked(serverPlayer)) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public Optional<HTMContainerLock> getLock() {
        return Optional.ofNullable(lock);
    }

    @Override
    public void setLock(HTMContainerLock lock) {
        this.lock = lock;
        markDirty();
    }
}
