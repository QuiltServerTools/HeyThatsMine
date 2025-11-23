package com.github.fabricservertools.htm.api;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum FlagType implements StringRepresentable {
    HOPPERS("hoppers", true),
    COPPER_GOLEMS("copper_golems", true);

    public static final Codec<FlagType> CODEC = StringRepresentable.fromEnum(FlagType::values);

    private final String id;
    private final boolean defaultValue;

    FlagType(String id, boolean defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getSerializedName() {
        return id;
    }

    public boolean defaultValue() {
        return defaultValue;
    }

    public Component displayName() {
        return Component.literal(id.toUpperCase()).withStyle(ChatFormatting.WHITE);
    }

    public static FlagType fromString(String flag) {
        for (FlagType type : values()) {
            if (type.id.equals(flag.toLowerCase())) {
                return type;
            }
        }
        return null;
    }
}
