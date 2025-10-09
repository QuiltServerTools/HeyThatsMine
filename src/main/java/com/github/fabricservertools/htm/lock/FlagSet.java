package com.github.fabricservertools.htm.lock;

import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.config.HTMConfig;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FlagSet {
    public static final FlagSet DEFAULT_FLAGS = new FlagSet(Arrays.stream(FlagType.values())
            .collect(Collectors.toMap(Function.identity(), FlagType::defaultValue)));
    public static final FlagSet EMPTY = new FlagSet(Map.of());

    private static final Codec<Pair<FlagType, Boolean>> SINGLE_FLAG_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    FlagType.CODEC.fieldOf("type").forGetter(Pair::getFirst),
                    Codec.BOOL.fieldOf("value").forGetter(Pair::getSecond)
            ).apply(instance, Pair::of)
    );
    public static final Codec<FlagSet> CODEC = SINGLE_FLAG_CODEC.listOf().xmap(
            list -> list.stream()
                    .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)),
            map -> map.entrySet().stream()
                    .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                    .toList())
            .xmap(FlagSet::new, set -> set.flags);
    public static final Codec<FlagSet> CONFIG_CODEC = Codec.unboundedMap(FlagType.CODEC, Codec.BOOL).xmap(FlagSet::new, set -> set.flags);

    private final EnumMap<FlagType, Boolean> flags;

    public FlagSet(Map<FlagType, Boolean> flags) {
        if (flags.isEmpty()) {
            this.flags = new EnumMap<>(FlagType.class);
        } else {
            this.flags = new EnumMap<>(flags);
        }
    }

    private FlagSet(EnumMap<FlagType, Boolean> flags) {
        this.flags = flags;
    }

    public boolean get(FlagType flag, BlockState state) {
        Boolean override = flags.get(flag);
        return override != null ? override : HTMConfig.get().defaultFlags().get(flag, state);
    }

    public Boolean getNoFallback(FlagType flag) {
        return flags.get(flag);
    }

    public FlagSet with(FlagType flag, boolean set) {
        if (flags.getOrDefault(flag, !set) == set) {
            return this;
        }
        EnumMap<FlagType, Boolean> copy = new EnumMap<>(flags);
        copy.put(flag, set);
        return new FlagSet(copy);
    }

    public FlagSet without(FlagType flag) {
        if (!flags.containsKey(flag)) {
            return this;
        } else if (flags.size() == 1) {
            return EMPTY;
        }
        EnumMap<FlagType, Boolean> copy = new EnumMap<>(flags);
        copy.remove(flag);
        return new FlagSet(copy);
    }

    public FlagSet expand() {
        EnumMap<FlagType, Boolean> copy = new EnumMap<>(flags);
        for (FlagType flag : FlagType.values()) {
            copy.putIfAbsent(flag, flag.defaultValue());
        }
        return new FlagSet(copy);
    }

    public void forEach(BlockState state, BiConsumer<FlagType, Boolean> consumer) {
        for (FlagType flag : FlagType.values()) {
            consumer.accept(flag, get(flag, state));
        }
    }
}
