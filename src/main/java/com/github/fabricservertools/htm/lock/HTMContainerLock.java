package com.github.fabricservertools.htm.lock;

import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.api.Lock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Uuids;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record HTMContainerLock(Lock lockData, UUID owner, Set<UUID> trusted, FlagSet flags) {
	public static final MapCodec<HTMContainerLock> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
					Lock.CODEC.forGetter(HTMContainerLock::lockData),
					Uuids.INT_STREAM_CODEC.fieldOf("Owner").forGetter(HTMContainerLock::owner),
					setOf(Uuids.INT_STREAM_CODEC.listOf()).fieldOf("Trusted").forGetter(HTMContainerLock::trusted),
					FlagSet.CODEC.fieldOf("Flags").forGetter(HTMContainerLock::flags)
			).apply(instance, HTMContainerLock::new)
	);
	public static final Codec<HTMContainerLock> CODEC = MAP_CODEC.codec();

	public HTMContainerLock(Lock type, ServerPlayerEntity owner) {
		this(type, owner.getUuid(), Set.of(), FlagSet.EMPTY);
	}

	private static <T> Codec<Set<T>> setOf(Codec<List<T>> listCodec) {
		return listCodec.xmap(Set::copyOf, List::copyOf);
	}

	public boolean canOpen(ServerPlayerEntity player) {
		if (lockData.canOpen(player, this)) return true;

		if (isOwner(player)) return true;

		player.sendMessage(HTMTexts.CONTAINER_LOCKED, true);
		player.playSoundToPlayer(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		return false;
	}

	public HTMContainerLock withType(Lock type) {
		return new HTMContainerLock(type, owner, trusted, flags);
	}

	public HTMContainerLock transfer(UUID id) {
		return new HTMContainerLock(lockData, id, trusted, flags);
	}

	public Optional<HTMContainerLock> withTrusted(UUID id) {
		Set<UUID> newTrusted = new HashSet<>(trusted);
		boolean added = newTrusted.add(id);
		if (added) {
			return Optional.of(new HTMContainerLock(lockData, owner, Set.copyOf(newTrusted), flags));
		}
		return Optional.empty();
	}

	public Optional<HTMContainerLock> withoutTrusted(UUID id) {
		Set<UUID> newTrusted = new HashSet<>(trusted);
		boolean removed = newTrusted.remove(id);
		if (removed) {
			return Optional.of(new HTMContainerLock(lockData, owner, Set.copyOf(newTrusted), flags));
		}
		return Optional.empty();
	}

	public HTMContainerLock withFlag(FlagType flag, boolean set) {
		return new HTMContainerLock(lockData, owner, trusted, flags.with(flag, set));
	}

    public HTMContainerLock withoutFlag(FlagType flag) {
        return new HTMContainerLock(lockData, owner, trusted, flags.without(flag));
    }

	public boolean isOwner(ServerPlayerEntity player) {
		if (!owner.equals(player.getUuid())) {
			if (Permissions.check(player, "htm.admin", 2)) {
				Utility.sendMessage(player, HTMTexts.CONTAINER_OVERRIDE.apply(Utility.getFormattedNameFromUUID(owner, player.getEntityWorld().getServer())));
				return true;
			}

			return false;
		}

		return true;
	}

	public boolean isTrusted(UUID id) {
		return trusted.contains(id);
	}

	public boolean flag(FlagType flag) {
		return flags.get(flag);
	}
}
