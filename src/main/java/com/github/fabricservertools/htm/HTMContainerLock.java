package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.api.LockType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class HTMContainerLock {
    private LockType type;
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
        HashMap<String, Boolean> hashMap = new HashMap();
        for (String flagType : HTMRegistry.getFlagTypes()) {
            hashMap.put(flagType, HTM.config.defaultFlags.getOrDefault(flagType, false));
        }

        flags = hashMap;
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (type != null) {
            tag.putString("Type", HTMRegistry.getNameFromLock(type));
            tag.put("TypeData", type.toTag());
            tag.putUuid("Owner", owner);

            ListTag trustedTag = new ListTag();
            for (UUID uuid : trusted) {
                trustedTag.add(NbtHelper.fromUuid(uuid));
            }

            tag.put("Trusted", trustedTag);

            ListTag flagsTag = new ListTag();
            for (Map.Entry<String, Boolean> entry : flags.entrySet()) {
                CompoundTag flagTag = new CompoundTag();
                flagTag.putString("type", entry.getKey());
                flagTag.putBoolean("value", entry.getValue());

                flagsTag.add(flagTag);
            }

            tag.put("Flags", flagsTag);
        }

        return tag;
    }

    public void fromTag(CompoundTag tag) {
        if (tag.contains("Type")) {
            try {
                type = HTMRegistry.getLockFromName(tag.getString("Type")).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                HTM.LOGGER.error("Failed to create lock type");
                type = null;
                return;
            }
            type.fromTag(tag.getCompound("TypeData"));
            owner = tag.getUuid("Owner");

            ListTag trustedTag = tag.getList("Trusted", 11);

            for(int i = 0; i < trustedTag.size(); ++i) {
                trusted.add(NbtHelper.toUuid(trustedTag.get(i)));
            }

            ListTag flagTags = tag.getList("Flags", 10);
            for (Tag flagTag : flagTags) {
                CompoundTag compoundTag = (CompoundTag) flagTag;
                flags.put(compoundTag.getString("type"), compoundTag.getBoolean("value"));
            }
        }
    }

    public boolean canOpen(ServerPlayerEntity player) {
        if (type == null) return true;

        if (type.canOpen(player, this)) return true;

        if (isOwner(player)) return true;

        player.sendMessage(new TranslatableText("text.htm.locked"), true);
        player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return false;
    }

    public LockType getType() {
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

    public void setType(LockType type, ServerPlayerEntity owner) {
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

    public void transfer(UUID id) {
        owner = id;
    }

    public boolean isOwner(ServerPlayerEntity player) {
        if (!owner.equals(player.getUuid())) {
            if (Permissions.check(player, "htm.admin", 4)) {
                player.sendMessage(new TranslatableText("text.htm.override",
                        player.getServerWorld().getServer().getUserCache().getByUuid(owner).getName()),
                        false);
                return true;
            }

            return false;
        }

        return true;
    }

    public boolean isLocked () {
        return owner != null;
    }

    public void setFlag(String flagType, boolean value) {
        flags.put(flagType, value);
    }
}
