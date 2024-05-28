package com.github.fabricservertools.htm.world.data;

import com.github.fabricservertools.htm.api.Group;
import com.github.fabricservertools.htm.api.LockGroup;
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
    private final HashMap<UUID, Group> groups;

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

        for (Group group : groups.values()) {
            NbtCompound groupTag = new NbtCompound();
            groupTag.putUuid(LockGroupId, group.getId());
            groupTag.putUuid(LockGroupOwner, group.getOwner());
            groupTag.putString(LockGroupName, group.getName());

            NbtList trustedUsers = new NbtList();
            for (UUID userId : group.getMembers()) {
                trustedUsers.add(NbtHelper.fromUuid(userId));
            }
            groupTag.put(LockGroupList, trustedUsers);

            NbtList managers = new NbtList();
            for (UUID userId : group.getManagers()) {
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

            lockGroupState.groups.put(groupId, new LockGroup(groupId, ownerId, trusted, managers, groupName));
        });

        return lockGroupState;
    }

    public boolean isTrusted(Set<UUID> groupIds, UUID playerId) {
        return groupIds.stream().anyMatch(it -> {
            Group group = groups.get(it);
            if (group == null) {
                return false;
            }

            if (group.getMembers().contains(playerId)) return true;
            if (group.getManagers().contains(playerId)) return true;
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
        Group group = groups.get(groupId);
        if (group == null) {
            return false;
        }

        if (group.addMember(trusted)) {
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

    public boolean addGroup(Group group) {
        if (groups.containsKey(group.getId())) {
            return false;
        }

        groups.put(group.getId(), group);
        markDirty();
        return true;
    }

    public boolean removeGroup(Group group) {
        if (groups.containsKey(group.getId())) {
            groups.remove(group.getId());
            markDirty();
            return true;
        }
        return false;
    }

    public ImmutableMap<UUID, Group> getGroups() {
        return ImmutableMap.copyOf(groups);
    }
}
