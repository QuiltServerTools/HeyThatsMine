package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.lock.type.KeyLock;
import com.github.fabricservertools.htm.lock.type.PrivateLock;
import com.github.fabricservertools.htm.lock.type.PublicLock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

public interface Lock {
    // A codec that dispatches a lock type (Type) from the "Type" key, and then uses the lock type's codec to create a map codec that has a "TypeData" key as field
    MapCodec<Lock> CODEC = Type.CODEC.dispatchMap("Type", Lock::type, type -> type.codec.fieldOf("TypeData"));

	boolean canOpen(ServerPlayer player, HTMContainerLock lock);

	default void onInfo(ServerPlayer player, HTMContainerLock lock) {}

    default Component displayName() {
        return type().displayName();
    }

	Type type();

    enum Type implements StringRepresentable {
        PRIVATE("private", PrivateLock.CODEC, PrivateLock.INSTANCE),
        PUBLIC("public", PublicLock.CODEC, PublicLock.INSTANCE),
        KEY("key", KeyLock.CODEC, KeyLock::fromMainHandItem);

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;
        private final Codec<? extends Lock> codec;
        private final Function<ServerPlayer, ? extends Lock> factory;

        Type(String name, Codec<? extends Lock> codec, Function<ServerPlayer, Lock> factory) {
            this.name = name;
            this.codec = codec;
            this.factory = factory;
        }

        Type(String name, Codec<? extends Lock> codec, Lock instance) {
            this(name, codec, player -> instance);
        }

        public Component displayName() {
            return Component.literal(uiName()).withStyle(ChatFormatting.WHITE);
        }

        public String uiName() {
            return name.toUpperCase(Locale.ROOT);
        }

        public Lock create(ServerPlayer player) {
            return factory.apply(player);
        }

        public static Type fromUiName(String name) {
            String lowercase = name.toLowerCase(Locale.ROOT);
            for (Type type : values()) {
                if (type.name.equals(lowercase)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
