package com.github.fabricservertools.htm.mixin.lockable;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableObject;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(BaseContainerBlockEntity.class)
public abstract class LockableContainerMixin extends BlockEntity implements LockableObject {
	@Unique
	private @Nullable HTMContainerLock htmContainerLock = null;

	public LockableContainerMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Inject(method = "loadAdditional", at = @At("HEAD"))
	private void fromTag(ValueInput input, CallbackInfo ci) {
		readLock(input, lock -> htmContainerLock = lock);
	}

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void toTag(ValueOutput output, CallbackInfo ci) {
        writeLock(output);
    }

	@Inject(method = "canOpen(Lnet/minecraft/world/entity/player/Player;)Z", at = @At("HEAD"), cancellable = true)
	private void checkUnlocked(Player player, CallbackInfoReturnable<Boolean> cir) {
		if (player instanceof ServerPlayer serverPlayer) {
			canOpen(serverPlayer).ifPresent(cir::setReturnValue);
		}
	}

	@Override
	public Optional<HTMContainerLock> getLock() {
		return Optional.ofNullable(htmContainerLock);
	}

	@Override
	public void setLock(@Nullable HTMContainerLock lock) {
		htmContainerLock = lock;
		setChanged();
	}
}
