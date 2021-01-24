package com.github.fabricservertools.htm;

import com.mojang.authlib.GameProfile;

public class HTMInteractAction {
    private final ActionType type;
    private final LockType setType;
    private final GameProfile targetPlayer;

    private HTMInteractAction(ActionType type, LockType createType, GameProfile trustPlayer) {
        this.type = type;
        this.setType = createType;
        this.targetPlayer = trustPlayer;
    }

    public static HTMInteractAction set(LockType createType) {
        return new HTMInteractAction(ActionType.SET, createType, null);
    }

    public static HTMInteractAction trust(GameProfile playerEntity) {
        return new HTMInteractAction(ActionType.TRUST, null, playerEntity);
    }

    public static HTMInteractAction transfer(GameProfile playerEntity) {
        return new HTMInteractAction(ActionType.TRANSFER, null, playerEntity);
    }

    public static HTMInteractAction remove() {
        return new HTMInteractAction(ActionType.REMOVE, null, null);
    }

    public static HTMInteractAction info() {
        return new HTMInteractAction(ActionType.INFO, null, null);
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

    enum ActionType {
        SET,
        TRUST,
        REMOVE,
        INFO,
        TRANSFER
    }
}
