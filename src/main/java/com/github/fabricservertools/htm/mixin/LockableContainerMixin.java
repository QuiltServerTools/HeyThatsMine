package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.github.fabricservertools.htm.LockableObject;

@Mixin(LockableContainerBlockEntity.class)
public abstract class LockableContainerMixin implements LockableObject {
    public HTMContainerLock htmContainerLock;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(BlockEntityType<?> blockEntityType, CallbackInfo ci) {
        htmContainerLock = new HTMContainerLock();
    }

    @Inject(method = "toTag", at = @At("HEAD"))
    private void toTag(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        htmContainerLock.toTag(tag);
    }

    @Inject(method = "fromTag", at = @At("HEAD"))
    private void fromTag(BlockState state, CompoundTag tag, CallbackInfo ci) {
        htmContainerLock.fromTag(tag);
    }

    @Inject(method = "checkUnlocked(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void checkUnlocked(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(htmContainerLock.canOpen((ServerPlayerEntity) player));
    }

    @Override
    public HTMContainerLock getLock() {
        return htmContainerLock;
    }
}
