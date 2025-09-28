package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.lock.type.KeyLock;
import com.github.fabricservertools.htm.lock.type.PrivateLock;
import com.github.fabricservertools.htm.lock.type.PublicLock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;
import java.util.function.Function;

public interface Lock {
    // A codec that dispatches a lock type (Type) from the "Type" key, and then uses the lock type's codec to create a map codec that has a "TypeData" key as field
    MapCodec<Lock> CODEC = Type.CODEC.dispatchMap("Type", Lock::type, type -> type.codec.fieldOf("TypeData"));

	boolean canOpen(ServerPlayerEntity player, HTMContainerLock lock);

	default void onInfo(ServerPlayerEntity player, HTMContainerLock lock) {}

    default Text displayName() {
        return type().displayName();
    }

	Type type();

    enum Type implements StringIdentifiable {
        PRIVATE("private", PrivateLock.CODEC, PrivateLock.INSTANCE),
        PUBLIC("public", PublicLock.CODEC, PublicLock.INSTANCE),
        KEY("key", KeyLock.CODEC, KeyLock::fromMainHandItem);

        public static final Codec<Type> CODEC = StringIdentifiable.createCodec(Type::values);

        private final String name;
        private final Codec<? extends Lock> codec;
        private final Function<ServerPlayerEntity, ? extends Lock> factory;

        Type(String name, Codec<? extends Lock> codec, Function<ServerPlayerEntity, Lock> factory) {
            this.name = name;
            this.codec = codec;
            this.factory = factory;
        }

        Type(String name, Codec<? extends Lock> codec, Lock instance) {
            // Weird Java generics :(
            this(name, codec, player -> instance);
        }

        public Text displayName() {
            return Text.literal(uiName()).formatted(Formatting.WHITE);
        }

        public String uiName() {
            return name.toUpperCase(Locale.ROOT);
        }

        public Lock create(ServerPlayerEntity player) {
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
        public String asString() {
            return name;
        }
    }
}
