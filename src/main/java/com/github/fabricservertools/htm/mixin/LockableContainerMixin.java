package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
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

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerMixin extends BlockEntity implements LockableObject {
	@Unique
	private HTMContainerLock htmContainerLock = null;

	public LockableContainerMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Inject(method = "writeData", at = @At("HEAD"))
	private void toTag(WriteView view, CallbackInfo ci) {
		view.putNullable("container_lock", HTMContainerLock.CODEC, htmContainerLock);
	}

	@Inject(method = "readData", at = @At("HEAD"))
	private void fromTag(ReadView view, CallbackInfo ci) {
		view.getOptionalString("Type").ifPresentOrElse(
				legacy -> {
					// Legacy, lock data was stored in the root NBT object before 1.21.5
					// Even Mojang thinks this shouldn't be done anymore and marked the method as deprecated, will probably end up removing this functionality
					// once this method is removed too
					view.read(HTMContainerLock.MAP_CODEC).ifPresentOrElse(
							lock -> htmContainerLock = lock,
							() -> HTM.LOGGER.warn("Failed to read legacy container lock data!") // Can't really do much here
					);
				},
				() -> view.read("container_lock", HTMContainerLock.CODEC).ifPresent(lock -> htmContainerLock = lock)
		);
	}

	@Inject(method = "checkUnlocked(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
	private void checkUnlocked(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		if (htmContainerLock != null) {
			cir.setReturnValue(htmContainerLock.canOpen((ServerPlayerEntity) player));
		}
	}

	@Override
	public Optional<HTMContainerLock> getLock() {
		return Optional.ofNullable(htmContainerLock);
	}

	@Override
	public void setLock(HTMContainerLock lock) {
		htmContainerLock = lock;
		markDirty();
	}
}
