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
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntity implements Clearable, MenuProvider, LockableObject {

    @Unique
    private HTMContainerLock lock = null;

    public LecternBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
    public Optional<HTMContainerLock> getLock() {
        return Optional.ofNullable(lock);
    }

    @Override
    public void setLock(HTMContainerLock lock) {
        this.lock = lock;
        setChanged();
    }
}
