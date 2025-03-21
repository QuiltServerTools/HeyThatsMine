package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.api.LockType;
import com.mojang.serialization.Dynamic;
import net.minecraft.MinecraftVersion;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class KeyLock implements Lock {
	private static final String ITEM_TAG = "Item";
	private ItemStack key;

	@Override
	public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
		if (lock.isTrusted(player.getUuid())) return true;
		if (Utility.getGlobalTrustState(player.server).isTrusted(lock.getOwner(), player.getUuid()))
			return true;

		ItemStack itemStack = player.getMainHandStack();
		return ItemStack.areItemsAndComponentsEqual(itemStack, key);
	}

	@Override
	public void onLockSet(ServerPlayerEntity player, HTMContainerLock lock) {
		key = player.getMainHandStack().copy();
		player.sendMessage(Text.translatable("text.htm.key_set", key.toHoverableText()), false);
	}

	@Override
	public void onInfo(ServerPlayerEntity player, HTMContainerLock lock) {
		player.sendMessage(Text.translatable("text.htm.key", key.toHoverableText()), false);
	}

	@Override
	public NbtCompound toTag(RegistryWrapper.WrapperLookup registryLookup) {
		NbtCompound saveTag = new NbtCompound();
		saveTag.putInt(SharedConstants.DATA_VERSION_KEY, MinecraftVersion.CURRENT.getSaveVersion().getId());
		saveTag.put(ITEM_TAG, ItemStack.OPTIONAL_CODEC, key);
		return saveTag;
	}

	@Override
	public void fromTag(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		NbtCompound itemTag = tag.getCompoundOrEmpty(ITEM_TAG);
		int dataVersion = tag.getInt(SharedConstants.DATA_VERSION_KEY, 3700);
		if (dataVersion <= 3700) {
			// 1.20.4 or older
			itemTag = tag;
		}

		itemTag = (NbtCompound) Schemas.getFixer().update(TypeReferences.ITEM_STACK,
				new Dynamic<>(NbtOps.INSTANCE, itemTag), dataVersion,
				MinecraftVersion.CURRENT.getSaveVersion().getId()).cast(NbtOps.INSTANCE);
		key = ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, itemTag).getOrThrow();
	}

	@Override
	public LockType<?> getType() {
		return LockType.KEY_LOCK;
	}
}
