package com.github.fabricservertools.htm.lock.type;

import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;

public record KeyLock(ItemStack key) implements Lock {
	// You're really not supposed to do it like this... but it works
	private static final Codec<ItemStack> VERSIONED_ITEM_STACK = Codec.INT.dispatch(SharedConstants.DATA_VERSION_TAG,
			stack -> SharedConstants.getCurrentVersion().dataVersion().version(), KeyLock::itemStackCodec);

	public static final Codec<KeyLock> CODEC = VERSIONED_ITEM_STACK.xmap(KeyLock::new, KeyLock::key);

	private static MapCodec<ItemStack> itemStackCodec(int dataVersion) {
		return Codec.of(ItemStack.OPTIONAL_CODEC,
                new Decoder<>() {
                    @Override
                    public <T> DataResult<Pair<ItemStack, T>> decode(DynamicOps<T> ops, T input) {
                        Dynamic<T> dynamic = new Dynamic<>(ops, input);
                        return ItemStack.OPTIONAL_CODEC.decode(DataFixers.getDataFixer().update(References.ITEM_STACK, dynamic, dataVersion, SharedConstants.getCurrentVersion().dataVersion().version()));
                    }
                }).fieldOf("Item");
	}

	@Override
	public boolean canOpen(ServerPlayer player, HTMContainerLock lock) {
		if (Utility.getGlobalTrustData(player.level().getServer()).isTrusted(lock.owner(), player.getUUID())
                || lock.isTrusted(player.getUUID())) {
            return true;
        }

		ItemStack itemStack = player.getMainHandItem();
		return ItemStack.isSameItemSameComponents(itemStack, key);
	}

	@Override
	public void onInfo(ServerPlayer player, HTMContainerLock lock) {
		player.displayClientMessage(HTMComponents.CONTAINER_KEY.apply(key.getDisplayName()), false);
	}

	@Override
	public Type type() {
		return Type.KEY;
	}

	public static KeyLock fromMainHandItem(ServerPlayer player) {
		ItemStack key = player.getMainHandItem().copy();
		player.displayClientMessage(HTMComponents.CONTAINER_KEY_SET.apply(key.getDisplayName()), false);
		return new KeyLock(key);
	}
}
