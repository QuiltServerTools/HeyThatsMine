package com.github.fabricservertools.htm.world.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.List;
import java.util.UUID;

public class GlobalTrustState extends PersistentState {
	private static final Codec<Pair<UUID, List<UUID>>> TRUSTER_CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Uuids.INT_STREAM_CODEC.fieldOf("Truster").forGetter(Pair::getFirst),
					Uuids.INT_STREAM_CODEC.listOf().fieldOf("Trusted").forGetter(Pair::getSecond)
			).apply(instance, Pair::of)
	);

	public static final Codec<GlobalTrustState> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					TRUSTER_CODEC.listOf().fieldOf("GlobalTrusts").forGetter(GlobalTrustState::globalTrusts)
			).apply(instance, GlobalTrustState::new)
	);

	public static final PersistentStateType<GlobalTrustState> TYPE = new PersistentStateType<>("globalTrust", GlobalTrustState::new, CODEC, null);

	private final Multimap<UUID, UUID> globalTrust = HashMultimap.create();

	private GlobalTrustState() {}

	private GlobalTrustState(List<Pair<UUID, List<UUID>>> globalTrusts) {
		for (Pair<UUID, List<UUID>> truster : globalTrusts) {
			globalTrust.putAll(truster.getFirst(), truster.getSecond());
		}
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

	private List<Pair<UUID, List<UUID>>> globalTrusts() {
		return globalTrust.asMap().entrySet().stream()
				.map(entry -> Pair.of(entry.getKey(), List.copyOf(entry.getValue())))
				.toList();
	}
}
