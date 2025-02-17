package com.github.fabricservertools.htm.api;

import java.util.Set;
import java.util.UUID;

/**
 * Describes a group which is able to be applied to protection and holds users.
 */
public interface ProtectionGroup {
    /**
     * Gets the unique id of this group.
     * @return The group id.
     */
    UUID getId();

    /**
     * Gets the display name of the collection.
     * @return The display name.
     */
    String getName();

    /**
     * Sets the name of the collection.
     * @param Name The new display name.
     */
    void setName(String Name);

    /**
     * Gets the id of the user who created this group.
     * @return The {@see java.util.UUID} of the user who created this group.
     */
    UUID getOwner();

    /**
     * Gets a readonly list of all group managers.
     * @return A readonly list of group managers.
     */
    Set<UUID> getManagers();

    /**
     * Makes the specified player a manager.
     * @param player The unique id of the player to add.
     * @return true if the player was added to the collection; otherwise,
     * false if the player was already a member of the collection.
     */
    boolean addManager(UUID player);

    /**
     * Removes the specified player being a group manager.
     * @param player The unique id of the player to remove.
     * @return true if the player was removed; otherwise, false.
     */
    boolean removeManager(UUID player);

    /**
     * Removes all players from the collection.
     */
    void clearManagers();

    /**
     * Gets a readonly list of all members in this collection.
     * @return A readonly list of all members in this collection.
     */
    Set<UUID> getMembers();

    /**
     * Adds the specified player to the collection.
     * @param player The unique id of the player to add.
     * @return true if the player was added to the collection; otherwise,
     * false if the player was already a member of the collection.
     */
    boolean addMember(UUID player);

    /**
     * Removes the specified player from the collection.
     * @param player The unique id of the player to remove.
     * @return true if the player was removed; otherwise, false.
     */
    boolean removeMember(UUID player);

    /**
     * Removes all players from the collection.
     */
    void clearMembers();
}
