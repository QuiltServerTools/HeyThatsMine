package com.github.fabricservertools.htm.mixin.lockable;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.ticks.ContainerSingleItem;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity implements ContainerSingleItem.BlockContainerSingleItem, LockableObject {

    @Unique
    private HTMContainerLock lock = null;

    public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    public void readLockData(ValueInput view, CallbackInfo ci) {
        readLock(view, lock -> this.lock = lock);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    public void writeLockData(ValueOutput view, CallbackInfo ci) {
        writeLock(view);
    }

    @Override
    public boolean stillValid(Player player) {
        if (player instanceof ServerPlayer serverPlayer && !canOpenUnchecked(serverPlayer)) {
            return false;
        }
        return BlockContainerSingleItem.super.stillValid(player);
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
