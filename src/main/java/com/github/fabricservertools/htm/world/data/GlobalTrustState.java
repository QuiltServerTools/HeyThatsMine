package com.github.fabricservertools.htm.world.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.util.UUID;

public class GlobalTrustState extends PersistentState {
	private final Multimap<UUID, UUID> globalTrust;

	public GlobalTrustState() {
		globalTrust = HashMultimap.create();
	}

	public static GlobalTrustState fromNbt(NbtCompound tag,
			RegistryWrapper.WrapperLookup registryLookup) {
		GlobalTrustState trustState = new GlobalTrustState();
		NbtList trustList = tag.getList("GlobalTrusts", NbtElement.COMPOUND_TYPE);

		trustList.forEach(tag1 -> {
			NbtCompound compoundTag = (NbtCompound) tag1;
			UUID truster = compoundTag.getUuid("Truster");

			NbtList trustedTag = compoundTag.getList("Trusted", NbtElement.INT_ARRAY_TYPE);

			for (NbtElement value : trustedTag) {
				trustState.globalTrust.put(truster, NbtHelper.toUuid(value));
			}
		});

		return trustState;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		NbtList trustList = new NbtList();

		for (UUID trusterID : globalTrust.keySet()) {
			NbtCompound trustTag = new NbtCompound();
			trustTag.putUuid("Truster", trusterID);

			NbtList trustedTag = new NbtList();
			for (UUID trustedID : globalTrust.get(trusterID)) {
				trustedTag.add(NbtHelper.fromUuid(trustedID));
			}

			trustTag.put("Trusted", trustedTag);

			trustList.add(trustTag);
		}

		tag.put("GlobalTrusts", trustList);
		return tag;
	}

	public boolean isTrusted(UUID truster, UUID trusted) {
		return globalTrust.get(truster).contains(trusted);
	}

	public boolean addTrust(UUID truster, UUID trusted) {
		markDirty();
		return globalTrust.put(truster, trusted);
	}

	public boolean removeTrust(UUID truster, UUID trusted) {
		markDirty();
		return globalTrust.remove(truster, trusted);
	}

	public Multimap<UUID, UUID> getTrusted() {
		return globalTrust;
	}
}
