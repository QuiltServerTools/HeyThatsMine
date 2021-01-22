package com.github.fabricservertools.htm;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;

import java.util.HashSet;
import java.util.UUID;

public class HTMContainerLock {
    private LockType type;
    private UUID owner;
    private HashSet<UUID> trusted;

    public HTMContainerLock() {
        type = null;
        owner = null;
        trusted = new HashSet<>();
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
        }
    }

    public boolean canOpen(ServerPlayerEntity player) {
        if (type == null) return true;

        if (player.getUuid().equals(owner)) return true;

        switch (type) {
            case PUBLIC:
                return true;

            case PRIVATE:
                if (trusted.contains(player.getUuid())) return true;
        }

        player.sendMessage(new LiteralText("Locked"), true);
        player.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return false;
    }

    public LockType getType() {
        return type;
    }

    public UUID getOwner() {
        return owner;
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

    public void addTrust(UUID id) {
        trusted.add(id);
    }
}
