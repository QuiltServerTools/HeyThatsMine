package com.github.fabricservertools.htm.api;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

/**
 * Describes a group which provides permissions globally.
 */
public class LockProtectionGroup implements ProtectionGroup {
    private final UUID id;
    private final UUID owner;
    private final HashSet<UUID> players;
    private final HashSet<UUID> managers;
    private String name;


    /**
     * Creates a new instance of @see LockGroup.
     * @param name The non-unique display name of the group.
     */
    public LockProtectionGroup(String name, ServerPlayerEntity owner) {
        this.id = UUID.randomUUID();
        this.owner = owner.getUuid();
        this.players = new HashSet<>();
        this.managers = new HashSet<>();
        this.name = name;
    }

    public LockProtectionGroup(UUID id, UUID owner, HashSet<UUID> players, HashSet<UUID> managers, String name) {
        this.id = id;
        this.owner = owner;
        this.players = players;
        this.managers = managers;
        this.name = name;
    }

    /**
     * Gets the unique id of this group.
     *
     * @return The group id.
     */
    @Override
    public UUID getId() {
        return this.id;
    }

    /**
     * Gets the display name of the collection.
     *
     * @return The display name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the collection.
     *
     * @param name The new display name.
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the id of the user who created this group.
     *
     * @return The {@see java.util.UUID} of the user who created this group.
     */
    @Override
    public UUID getOwner() {
        return owner;
    }

    /**
     * Gets a readonly list of all group managers.
     *
     * @return A readonly list of group managers.
     */
    @Override
    public Set<UUID> getManagers() {
        return Collections.unmodifiableSet(managers);
}

    /**
     * Makes the specified player a manager.
     *
     * @param player The id of the player to add.
     * @return true if the player was added to the collection; otherwise,
     * false if the player was already a member of the collection.
     */
    @Override
    public boolean addManager(UUID player) {
        return managers.add(player);
    }

    /**
     * Removes the specified player being a group manager.
     *
     * @param player The unique id of the player to remove.
     * @return true if the player was removed; otherwise, false.
     */
    @Override
    public boolean removeManager(UUID player) {
        return managers.remove(player);
    }

    /**
     * Removes all players from the collection.
     */
    @Override
    public void clearManagers() {
        managers.clear();
    }

    /**
     * Gets a readonly list of all members in this collection.
     *
     * @return A readonly list of all members in this collection.
     */
    @Override
    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(players);
    }

    /**
     * Adds the specified player to the collection.
     *
     * @param player The unique id of the player to add.
     * @return true if the player was added to the collection; otherwise,
     * false if the player was already a member of the collection.
     */
    @Override
    public boolean addMember(UUID player) {
        return players.add(player);
    }

    /**
     * Removes the specified player from the collection.
     *
     * @param player The unique id of the player to remove.
     * @return true if the player was removed; otherwise, false.
     */
    @Override
    public boolean removeMember(UUID player) {
        return players.remove(player);
    }

    /**
     * Removes all players from the collection.
     */
    @Override
    public void clearMembers() {
        players.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LockProtectionGroup lockGroup)) {
            return false;
        }
        return Objects.equals(id, lockGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
