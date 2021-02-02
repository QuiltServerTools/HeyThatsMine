package com.github.fabricservertools.htm;

import com.mojang.authlib.GameProfile;

public class HTMInteractAction {
    private final ActionType type;
    private final LockType setType;
    private final HTMContainerLock.FlagType flagType;
    private final GameProfile targetPlayer;
    private boolean bool;

    private HTMInteractAction(ActionType type, LockType createType, HTMContainerLock.FlagType flagType, GameProfile trustPlayer) {
        this.type = type;
        this.setType = createType;
        this.flagType = flagType;
        this.targetPlayer = trustPlayer;
    }

    public static HTMInteractAction set(LockType createType) {
        return new HTMInteractAction(ActionType.SET, createType, null, null);
    }

    public static HTMInteractAction trust(GameProfile playerEntity) {
        return new HTMInteractAction(ActionType.TRUST, null, null, playerEntity);
    }

    public static HTMInteractAction transfer(GameProfile playerEntity) {
        return new HTMInteractAction(ActionType.TRANSFER, null, null, playerEntity);
    }

    public static HTMInteractAction remove() {
        return new HTMInteractAction(ActionType.REMOVE, null, null, null);
    }

    public static HTMInteractAction info() {
        return new HTMInteractAction(ActionType.INFO, null, null, null);
    }

    public static HTMInteractAction flag(HTMContainerLock.FlagType flagType, boolean bool) {
        HTMInteractAction action = new HTMInteractAction(ActionType.FLAG, null, flagType, null);
        action.bool = bool;
        return action;
    }

    public ActionType getType() {
        return type;
    }

    public LockType getSetType() {
        return setType;
    }

    public GameProfile getTargetPlayer() {
        return targetPlayer;
    }

    public HTMContainerLock.FlagType getFlagType() {
        return flagType;
    }

    public boolean getBool() {
        return bool;
    }

    enum ActionType {
        SET,
        TRUST,
        REMOVE,
        INFO,
        TRANSFER,
        FLAG
    }
}
