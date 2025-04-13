package com.github.fabricservertools.htm.locks;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.Lock;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.MinecraftVersion;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public record KeyLock(ItemStack key) implements Lock {
	// You're really not supposed to do it like this... but it works
	private static final Codec<ItemStack> VERSIONED_ITEM_STACK = Codec.INT.dispatch(SharedConstants.DATA_VERSION_KEY,
			stack -> MinecraftVersion.CURRENT.getSaveVersion().getId(), KeyLock::itemStackCodec);

	public static final Codec<KeyLock> CODEC = VERSIONED_ITEM_STACK.xmap(KeyLock::new, KeyLock::key);

	private static MapCodec<ItemStack> itemStackCodec(int dataVersion) {
		return Codec.of(ItemStack.OPTIONAL_CODEC,
                new Decoder<>() {
                    @Override
                    public <T> DataResult<Pair<ItemStack, T>> decode(DynamicOps<T> ops, T input) {
                        Dynamic<T> dynamic = new Dynamic<>(ops, input);
                        return ItemStack.OPTIONAL_CODEC.decode(Schemas.getFixer().update(TypeReferences.ITEM_STACK, dynamic, dataVersion, MinecraftVersion.CURRENT.getSaveVersion().getId()));
                    }
                }).fieldOf("Item");
	}

	@Override
	public boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock) {
		if (lock.isTrusted(player.getUuid())) return true;
		if (Utility.getGlobalTrustState(player.server).isTrusted(lock.owner(), player.getUuid()))
			return true;

		ItemStack itemStack = player.getMainHandStack();
		return ItemStack.areItemsAndComponentsEqual(itemStack, key);
	}

	@Override
	public Lock withOwner(ServerPlayerEntity player) {
		ItemStack key = player.getMainHandStack().copy();
		player.sendMessage(Text.translatable("text.htm.key_set", key.toHoverableText()), false);
		return new KeyLock(key);
	}

	@Override
	public void onInfo(ServerPlayerEntity player, HTMContainerLock lock) {
		player.sendMessage(Text.translatable("text.htm.key", key.toHoverableText()), false);
	}

	@Override
	public Codec<KeyLock> codec() {
		return CODEC;
	}
}
