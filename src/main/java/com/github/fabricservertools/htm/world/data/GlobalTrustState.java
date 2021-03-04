package com.github.fabricservertools.htm.world.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.PersistentState;

import java.util.UUID;

public class GlobalTrustState extends PersistentState {
	private Multimap<UUID, UUID> globalTrust;

	public GlobalTrustState() {
		super("globalTrust");
		globalTrust = HashMultimap.create();
	}

	@Override
	public void fromTag(CompoundTag tag) {
		ListTag trustList = tag.getList("GlobalTrusts", 10);

		trustList.forEach(tag1 -> {
			CompoundTag compoundTag = (CompoundTag) tag1;
			UUID truster = compoundTag.getUuid("Truster");

			ListTag trustedTag = compoundTag.getList("Trusted", 11);

			for (int i = 0; i < trustedTag.size(); ++i) {
				globalTrust.put(truster, NbtHelper.toUuid(trustedTag.get(i)));
			}
		});
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		ListTag trustList = new ListTag();

		for (UUID trusterID : globalTrust.keySet()) {
			CompoundTag trustTag = new CompoundTag();
			trustTag.putUuid("Truster", trusterID);

			ListTag trustedTag = new ListTag();
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
