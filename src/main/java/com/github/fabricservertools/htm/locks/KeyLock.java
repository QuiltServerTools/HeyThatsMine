package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockType;
import com.github.fabricservertools.htm.world.data.GlobalTrustState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class KeyLock implements LockType {
	private ItemStack key;

	@Override
	public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
		if (lock.getTrusted().contains(player.getUuid())) return true;
		if (player.getServer().getOverworld().getPersistentStateManager().getOrCreate(GlobalTrustState::new, "globalTrust").isTrusted(lock.getOwner(), player.getUuid()))
			return true;

		ItemStack itemStack = player.getMainHandStack();
		if (itemStack.getItem() == key.getItem() && ItemStack.areTagsEqual(itemStack, key)) return true;

		return false;
	}

	@Override
	public void onLockSet(ServerPlayerEntity player, HTMContainerLock lock) {
		key = player.getMainHandStack().copy();
		player.sendMessage(new TranslatableText("text.htm.key_set", key.toHoverableText()), false);
	}

	@Override
	public void onInfo(ServerPlayerEntity player, HTMContainerLock lock) {
		player.sendMessage(new TranslatableText("text.htm.key", key.toHoverableText()), false);
	}

	@Override
	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		key.toTag(tag);
		return tag;
	}

	@Override
	public void fromTag(CompoundTag tag) {
		key = ItemStack.fromTag(tag);
	}
}
