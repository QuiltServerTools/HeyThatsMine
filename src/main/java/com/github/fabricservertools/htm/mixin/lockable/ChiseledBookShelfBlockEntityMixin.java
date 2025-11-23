package com.github.fabricservertools.htm.mixin.lockable;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(ChiseledBookShelfBlockEntity.class)
public abstract class ChiseledBookShelfBlockEntityMixin extends BlockEntity implements Container, LockableObject {

    @Unique
    private HTMContainerLock lock = null;

    public ChiseledBookShelfBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    public void readLockData(ValueInput input, CallbackInfo ci) {
        readLock(input, lock -> this.lock = lock);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    public void writeLockData(ValueOutput output, CallbackInfo ci) {
        writeLock(output);
    }

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    public void checkLock(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof ServerPlayer serverPlayer && !canOpenUnchecked(serverPlayer)) {
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
        setChanged();
    }
}
