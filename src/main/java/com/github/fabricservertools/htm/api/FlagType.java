package com.github.fabricservertools.htm.api;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum FlagType implements StringIdentifiable {
    HOPPERS("hoppers");

    public static final Codec<FlagType> CODEC = StringIdentifiable.createCodec(FlagType::values);

    private final String id;

    FlagType(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return id;
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
