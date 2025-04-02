package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableObject;
import com.mojang.serialization.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
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

	@Inject(method = "writeNbt", at = @At("HEAD"))
	private void toTag(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
		nbt.putNullable("container_lock", HTMContainerLock.CODEC, RegistryOps.of(NbtOps.INSTANCE, registryLookup), htmContainerLock);
	}

	@Inject(method = "readNbt", at = @At("HEAD"))
	private void fromTag(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup, CallbackInfo ci) {
		DynamicOps<NbtElement> ops = RegistryOps.of(NbtOps.INSTANCE, registryLookup);
		if (nbt.contains("Type")) {
			// Legacy
			HTMContainerLock.CODEC.parse(ops, nbt)
					.ifSuccess(lock -> htmContainerLock = lock)
					.ifError(error -> HTM.LOGGER.warn("Failed to read legacy container lock data! {}", error.message()));
		} else {
			nbt.get("container_lock", HTMContainerLock.CODEC, ops).ifPresent(lock -> htmContainerLock = lock);
		}
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
