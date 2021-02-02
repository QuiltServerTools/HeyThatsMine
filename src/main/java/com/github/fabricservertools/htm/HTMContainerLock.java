package com.github.fabricservertools.htm;

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
    private Map<FlagType, Boolean> flags;

    public HTMContainerLock() {
        type = null;
        owner = null;
        trusted = new HashSet<>();
        flags = getDefaultFlags();
    }

    private HashMap<FlagType, Boolean> getDefaultFlags() {
        HashMap<FlagType, Boolean> hashMap = new HashMap();
        hashMap.put(FlagType.HOPPERS, true);

        return hashMap;
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (type != null) {
            tag.putString("Type", this.type.name());
            tag.putUuid("Owner", owner);

            ListTag trustedTag = new ListTag();
            for (UUID uuid : trusted) {
                trustedTag.add(NbtHelper.fromUuid(uuid));
            }

            tag.put("Trusted", trustedTag);

            ListTag flagsTag = new ListTag();
            for (Map.Entry<FlagType, Boolean> entry : flags.entrySet()) {
                CompoundTag flagTag = new CompoundTag();
                flagTag.putString("type", entry.getKey().name());
                flagTag.putBoolean("value", entry.getValue());

                flagsTag.add(flagTag);
            }

            tag.put("Flags", flagsTag);
        }

        return tag;
    }

    public void fromTag(CompoundTag tag) {
        if (tag.contains("Type")) {
            type = LockType.valueOf(tag.getString("Type"));
            owner = tag.getUuid("Owner");

            ListTag trustedTag = tag.getList("Trusted", 11);

            for(int i = 0; i < trustedTag.size(); ++i) {
                trusted.add(NbtHelper.toUuid(trustedTag.get(i)));
            }

            ListTag flagTags = tag.getList("Flags", 10);
            for (Tag flagTag : flagTags) {
                CompoundTag compoundTag = (CompoundTag) flagTag;
                flags.put(FlagType.valueOf(compoundTag.getString("type")), compoundTag.getBoolean("value"));
            }
        }
    }

    public boolean canOpen(ServerPlayerEntity player) {
        if (type == null) return true;

        switch (type) {
            case PUBLIC:
                return true;

            case PRIVATE:
                if (trusted.contains(player.getUuid())) return true;
        }

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

    public Map<FlagType, Boolean> getFlags() {
        return flags;
    }

    public HashSet<UUID> getTrusted() {
        return trusted;
    }

    public void setType(LockType type, ServerPlayerEntity owner) {
        this.type = type;
        this.owner = owner.getUuid();
    }

    public void remove() {
        type = null;
        owner = null;
        trusted = new HashSet<>();
    }

    public boolean addTrust(UUID id) {
        return trusted.add(id);
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

            player.sendMessage(new TranslatableText("text.htm.error.not_owner"), false);
            return false;
        }

        return true;
    }

    public boolean isLocked () {
        return owner != null;
    }

    public void setflag(FlagType flagType, boolean value) {
        flags.put(flagType, value);
    }

    public enum FlagType {
        HOPPERS
    }
}
