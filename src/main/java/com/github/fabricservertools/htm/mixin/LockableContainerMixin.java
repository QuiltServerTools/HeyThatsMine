package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerMixin implements LockableObject {
	public HTMContainerLock htmContainerLock;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		htmContainerLock = new HTMContainerLock();
	}

	@Inject(method = "writeNbt", at = @At("HEAD"))
	private void toTag(NbtCompound tag, CallbackInfoReturnable<NbtCompound> cir) {
		htmContainerLock.toTag(tag);
	}

	@Inject(method = "readNbt", at = @At("HEAD"))
	private void fromTag(NbtCompound nbt, CallbackInfo ci) {
		htmContainerLock.fromTag(nbt);
	}

	@Inject(method = "checkUnlocked(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
	private void checkUnlocked(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(htmContainerLock.canOpen((ServerPlayerEntity) player));
	}

	@Override
	public HTMContainerLock getLock() {
		return htmContainerLock;
	}

	@Override
	public void setLock(HTMContainerLock lock) {
		htmContainerLock = lock;
	}
}
