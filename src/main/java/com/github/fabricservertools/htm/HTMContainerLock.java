package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.api.Lock;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class HTMContainerLock {
	private Lock type;
	private UUID owner;
	private HashSet<UUID> trusted;
	private Map<String, Boolean> flags;

	public HTMContainerLock() {
		type = null;
		owner = null;
		trusted = new HashSet<>();
		initFlags();
	}

	private void initFlags() {
		HashMap<String, Boolean> hashMap = new HashMap<>();
		for (String flagType : HTMRegistry.getFlagTypes()) {
			hashMap.put(flagType, HTM.config.defaultFlags.getOrDefault(flagType, false));
		}

		flags = hashMap;
	}

	public void toTag(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		if (type != null) {
			tag.putString("Type", HTMRegistry.getLockId(type.getType()));
			tag.put("TypeData", type.toTag(registryLookup));
			tag.put("Owner", Uuids.INT_STREAM_CODEC, owner);

			NbtList trustedTag = new NbtList();
			for (UUID uuid : trusted) {
				trustedTag.add(Uuids.INT_STREAM_CODEC.encodeStart(NbtOps.INSTANCE, uuid).getOrThrow());
			}

			tag.put("Trusted", trustedTag);

			NbtList flagsTag = new NbtList();
			for (Map.Entry<String, Boolean> entry : flags.entrySet()) {
				NbtCompound flagTag = new NbtCompound();
				flagTag.putString("type", entry.getKey());
				flagTag.putBoolean("value", entry.getValue());

				flagsTag.add(flagTag);
			}

			tag.put("Flags", flagsTag);
		}

	}

	public void fromTag(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		if (tag.contains("Type")) {
			try {
				type = HTMRegistry.getLock(tag.getString("Type", "")).orElseThrow(RuntimeException::new);
			} catch (Exception e) {
				HTM.LOGGER.error("Failed to create lock type: " + tag.getString("Type"));
				type = null;
				return;
			}
			type.fromTag(tag.getCompoundOrEmpty("TypeData"), registryLookup);
			owner = tag.get("Owner", Uuids.INT_STREAM_CODEC).orElse(Util.NIL_UUID);

			NbtList trustedTag = tag.getListOrEmpty("Trusted");

			for (NbtElement value : trustedTag) {
				trusted.add(Uuids.INT_STREAM_CODEC.parse(NbtOps.INSTANCE, value).getOrThrow());
			}

			NbtList flagTags = tag.getListOrEmpty("Flags");
			for (NbtElement flagTag : flagTags) {
				NbtCompound compoundTag = (NbtCompound) flagTag;
				flags.put(compoundTag.getString("type", ""), compoundTag.getBoolean("value", false));
			}
		}
	}

	public boolean canOpen(ServerPlayerEntity player) {
		if (type == null) return true;

		if (type.canOpen(player, this)) return true;

		if (isOwner(player)) return true;

		player.sendMessage(Text.translatable("text.htm.locked"), true);
		player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, 1.0F, 1.0F);
		return false;
	}

	public Lock getType() {
		return type;
	}

	public UUID getOwner() {
		return owner;
	}

	public Map<String, Boolean> getFlags() {
		return flags;
	}

	public HashSet<UUID> getTrusted() {
		return trusted;
	}

	public void setType(Lock type, ServerPlayerEntity owner) {
		this.type = type;
		this.owner = owner.getUuid();
		type.onLockSet(owner, this);
	}

	public void remove() {
		type = null;
		owner = null;
		trusted = new HashSet<>();
		initFlags();
	}

	public boolean addTrust(UUID id) {
		return trusted.add(id);
	}

	public boolean removeTrust(UUID id) {
		return trusted.remove(id);
	}

	public boolean isTrusted(UUID id) {
		return trusted.contains(id);
	}

	public void transfer(UUID id) {
		owner = id;
	}

	public boolean isOwner(ServerPlayerEntity player) {
		if (!owner.equals(player.getUuid())) {
			if (Permissions.check(player, "htm.admin", 2)) {
				String name = Utility.getNameFromUUID(owner, player.server);

				Utility.sendMessage(player, Text.translatable("text.htm.override", name));
				return true;
			}

			return false;
		}

		return true;
	}

	public boolean isLocked() {
		return owner != null;
	}

	public void setFlag(String flagType, boolean value) {
		flags.put(flagType, value);
	}
}
