package com.github.fabricservertools.htm.world.data;

import com.github.fabricservertools.htm.api.ProtectionGroup;
import com.github.fabricservertools.htm.api.LockProtectionGroup;
import com.google.common.collect.*;
import net.minecraft.nbt.*;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Describes a persistent state which stores groups.
 */
public class LockGroupState extends PersistentState {
    private final HashMap<UUID, ProtectionGroup> groups;

    private static final String LockGroups = "LockGroups";
    private static final String LockGroupId = "LockGroupId";
    private static final String LockGroupOwner = "LockGroupOwner";
    private static final String LockGroupName = "LockGroupName";
    private static final String LockGroupList = "LockGroupList";
    private static final String LockGroupManagers = "LockGroupManagers";

    public LockGroupState() {
        groups = new HashMap<>();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList trustList = new NbtList();

        for (ProtectionGroup protectionGroup : groups.values()) {
            NbtCompound groupTag = new NbtCompound();
            groupTag.putUuid(LockGroupId, protectionGroup.getId());
            groupTag.putUuid(LockGroupOwner, protectionGroup.getOwner());
            groupTag.putString(LockGroupName, protectionGroup.getName());

            NbtList trustedUsers = new NbtList();
            for (UUID userId : protectionGroup.getMembers()) {
                trustedUsers.add(NbtHelper.fromUuid(userId));
            }
            groupTag.put(LockGroupList, trustedUsers);

            NbtList managers = new NbtList();
            for (UUID userId : protectionGroup.getManagers()) {
                managers.add(NbtHelper.fromUuid(userId));
            }
            groupTag.put(LockGroupManagers, managers);

            trustList.add(groupTag);
        }

        nbt.put(LockGroups, trustList);
        return nbt;
    }

    public static LockGroupState fromNbt(NbtCompound tag) {
        LockGroupState lockGroupState = new LockGroupState();
        NbtList lockGroupList = tag.getList(LockGroups, NbtElement.COMPOUND_TYPE);

        lockGroupList.forEach(it -> {
            NbtCompound compoundTag = (NbtCompound)it;
            UUID groupId = compoundTag.getUuid(LockGroupId);
            UUID ownerId = compoundTag.getUuid(LockGroupOwner);
            String groupName = compoundTag.getString(LockGroupName);
            HashSet<UUID> trusted = new HashSet<>();
            HashSet<UUID> managers = new HashSet<>();

            NbtList trustedList = compoundTag.getList(LockGroupList, NbtElement.INT_ARRAY_TYPE);
            for (NbtElement value : trustedList) {
                trusted.add(NbtHelper.toUuid(value));
            }

            NbtList managerList = compoundTag.getList(LockGroupManagers, NbtElement.INT_ARRAY_TYPE);
            for (NbtElement value : managerList) {
                managers.add(NbtHelper.toUuid(value));
            }

            lockGroupState.groups.put(groupId, new LockProtectionGroup(groupId, ownerId, trusted, managers, groupName));
        });

        return lockGroupState;
    }

    public boolean isTrusted(Set<UUID> groupIds, UUID playerId) {
        return groupIds.stream().anyMatch(it -> {
            ProtectionGroup protectionGroup = groups.get(it);
            if (protectionGroup == null) {
                return false;
            }

            if (protectionGroup.getMembers().contains(playerId)) return true;
            if (protectionGroup.getManagers().contains(playerId)) return true;
            return false;
        });
    }

    public boolean isMember(UUID groupId, UUID trusted) {
        return groups.get(groupId).getMembers().contains(trusted);
    }

    public boolean isManager(UUID groupId, UUID manager) {
        return groups.get(groupId).getManagers().contains(manager);
    }

    public boolean addToGroup(UUID groupId, UUID trusted) {
        ProtectionGroup protectionGroup = groups.get(groupId);
        if (protectionGroup == null) {
            return false;
        }

        if (protectionGroup.addMember(trusted)) {
            markDirty();
            return true;
        }

        return false;
    }

    public boolean removeFromGroup(UUID group, UUID trusted) {
        if(groups.remove(group, trusted)) {
            markDirty();
            return true;
        }
        return false;
    }

    public boolean addGroup(ProtectionGroup protectionGroup) {
        if (groups.containsKey(protectionGroup.getId())) {
            return false;
        }

        groups.put(protectionGroup.getId(), protectionGroup);
        markDirty();
        return true;
    }

    public boolean removeGroup(ProtectionGroup protectionGroup) {
        if (groups.containsKey(protectionGroup.getId())) {
            groups.remove(protectionGroup.getId());
            markDirty();
            return true;
        }
        return false;
    }

    public ImmutableMap<UUID, ProtectionGroup> getGroups() {
        return ImmutableMap.copyOf(groups);
    }
}
