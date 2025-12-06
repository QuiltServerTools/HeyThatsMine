package com.github.fabricservertools.htm.lock;

import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.config.SingleBlockSelector;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public record BlockFlagSet(Map<SingleBlockSelector, FlagSet> overrides, FlagSet fallback, BlockFlagPredicate flagPredicate) {
    private static final Codec<Map<SingleBlockSelector, FlagSet>> OVERRIDES_CODEC = Codec.unboundedMap(SingleBlockSelector.CODEC, FlagSet.CONFIG_CODEC);
    public static final Codec<BlockFlagSet> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    OVERRIDES_CODEC.fieldOf("overrides").forGetter(BlockFlagSet::overrides),
                    FlagSet.CONFIG_CODEC.fieldOf("default").forGetter(BlockFlagSet::fallback)
            ).apply(instance, BlockFlagSet::new)
    );
    public static final Codec<BlockFlagSet> SAFE_CODEC = Codec.withAlternative(CODEC, FlagSet.CONFIG_CODEC,
            fallback -> new BlockFlagSet(Map.of(), fallback));

    public static final BlockFlagSet DEFAULT = new BlockFlagSet(Map.of(), FlagSet.DEFAULT_FLAGS);

    public BlockFlagSet(Map<SingleBlockSelector, FlagSet> overrides, FlagSet fallback) {
        this(Map.copyOf(overrides), fallback.expand(), createFlagPredicate(overrides, fallback));
    }

    public boolean get(FlagType flag, BlockState state) {
        return flagPredicate.test(flag, BuiltInRegistries.BLOCK.wrapAsHolder(state.getBlock()));
    }

    private static BlockFlagPredicate createFlagPredicate(Map<SingleBlockSelector, FlagSet> overrides, FlagSet fallback) {
        Iterator<Map.Entry<SingleBlockSelector, FlagSet>> overridesIterator = overrides.entrySet().iterator();
        BlockFlagPredicate builder = new BlockFlagPredicate((flag, block) -> fallback.getNoFallback(flag));
        while (overridesIterator.hasNext()) {
            builder = new BlockFlagPredicate(overridesIterator.next(), builder);
        }
        return builder;
    }

    private record BlockFlagPredicate(BiFunction<FlagType, Holder<Block>, @Nullable Boolean> predicate,
                                      @Nullable BlockFlagPredicate fallback) implements BiPredicate<FlagType, Holder<Block>> {

        private BlockFlagPredicate(Map.Entry<SingleBlockSelector, FlagSet> entry, BlockFlagPredicate fallback) {
            this((flag, block) -> {
                if (entry.getKey().is(block)) {
                    return entry.getValue().getNoFallback(flag);
                }
                return null;
            }, fallback);
        }

        private BlockFlagPredicate(BiPredicate<FlagType, Holder<Block>> fallback) {
            this(fallback::test, null);
        }

        @Override
        public boolean test(FlagType flag, Holder<Block> block) {
            Boolean override = predicate.apply(flag, block);
            if (override != null) {
                return override;
            }
            return Objects.requireNonNull(fallback, "fallback of BlockFlagPredicate may not be null when the predicate itself returns null").test(flag, block);
        }
    }
}
