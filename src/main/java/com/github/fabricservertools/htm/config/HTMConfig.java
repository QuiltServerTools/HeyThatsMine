package com.github.fabricservertools.htm.config;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.lock.BlockFlagSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public record HTMConfig(boolean canTrustedPlayersBreakChests, BlockFlagSet defaultFlags, Map<SingleBlockSelector, Lock.Type> autoLockingContainers) {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final Path CONFIG_PATH = Path.of("htm_config.json");

    private static final Codec<Map<SingleBlockSelector, Lock.Type>> AUTO_LOCKING_CONTAINERS_LEGACY_CODEC = SingleBlockSelector.CODEC.listOf()
            .xmap(list -> list.stream().collect(Collectors.toMap(Function.identity(), selector -> Lock.Type.PRIVATE)),
                    map -> List.copyOf(map.keySet()));
    private static final Codec<Map<SingleBlockSelector, Lock.Type>> AUTO_LOCKING_CONTAINERS_MODERN_CODEC = Codec.unboundedMap(SingleBlockSelector.CODEC, Lock.Type.CODEC);
    private static final Codec<Map<SingleBlockSelector, Lock.Type>> AUTO_LOCKING_CONTAINERS_CODEC = Codec.withAlternative(AUTO_LOCKING_CONTAINERS_MODERN_CODEC, AUTO_LOCKING_CONTAINERS_LEGACY_CODEC);

    public static final Codec<HTMConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("can_trusted_players_break_chests").forGetter(HTMConfig::canTrustedPlayersBreakChests),
                    BlockFlagSet.SAFE_CODEC.fieldOf("default_flags").forGetter(HTMConfig::defaultFlags),
                    AUTO_LOCKING_CONTAINERS_CODEC.fieldOf("auto_locking_containers").forGetter(HTMConfig::autoLockingContainers)
            ).apply(instance, HTMConfig::new)
    );
    public static final Codec<HTMConfig> LEGACY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("canTrustedPlayersBreakChests").forGetter(HTMConfig::canTrustedPlayersBreakChests),
                    BlockFlagSet.SAFE_CODEC.fieldOf("defaultFlags").forGetter(HTMConfig::defaultFlags),
                    AUTO_LOCKING_CONTAINERS_CODEC.fieldOf("autolockingContainers").forGetter(HTMConfig::autoLockingContainers)
            ).apply(instance, HTMConfig::new)
    );
    public static final Codec<HTMConfig> SAFE_CODEC = Codec.withAlternative(CODEC, LEGACY_CODEC);

    private static final Map<SingleBlockSelector, Lock.Type> DEFAULT_AUTO_LOCKING_CONTAINERS = Map.of(
            new SingleBlockSelector(Blocks.CHEST), Lock.Type.PRIVATE,
            new SingleBlockSelector(Blocks.TRAPPED_CHEST), Lock.Type.PRIVATE,
            new SingleBlockSelector(Blocks.BARREL), Lock.Type.PRIVATE,
            new SingleBlockSelector(Blocks.FURNACE), Lock.Type.PRIVATE,
            new SingleBlockSelector(Blocks.BLAST_FURNACE), Lock.Type.PRIVATE,
            new SingleBlockSelector(Blocks.SMOKER), Lock.Type.PRIVATE,
            new SingleBlockSelector(BlockTags.SHULKER_BOXES), Lock.Type.PRIVATE,
            new SingleBlockSelector(BlockTags.COPPER_CHESTS), Lock.Type.PRIVATE);
    private static final HTMConfig DEFAULT_CONFIG = new HTMConfig(false, BlockFlagSet.DEFAULT, DEFAULT_AUTO_LOCKING_CONTAINERS);

    private static @Nullable HTMConfig loaded = null;

    public Optional<Lock.Type> getAutoLockingType(BlockState block) {
        for (Map.Entry<SingleBlockSelector, Lock.Type> container : autoLockingContainers.entrySet()) {
            if (container.getKey().is(block)) {
                return Optional.of(container.getValue());
            }
        }
        return Optional.empty();
    }

    private void save() {
        try {
            Files.writeString(getConfigPath(), GSON.toJson(CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow()));
        } catch (IOException exception) {
            HTM.LOGGER.error("Failed to save config!", exception);
        } catch (IllegalStateException exception) {
            HTM.LOGGER.error("Failed to encode config!", exception);
        }
    }

    public static void load() {
        if (loaded != null) {
            return;
        } else if (!Files.exists(getConfigPath())) {
            loaded = DEFAULT_CONFIG;
            loaded.save();
        }

        try {
            loaded = SAFE_CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(getConfigPath()))).getOrThrow();
            loaded.save();
        } catch (IOException | IllegalStateException exception) {
            HTM.LOGGER.error("Failed to read config, using default one!", exception);
            HTM.LOGGER.error("Please fix the errors above!");
            loaded = DEFAULT_CONFIG;
        }
    }

    public static HTMConfig get() {
        if (loaded == null) {
            throw new IllegalStateException("Tried to access config before it was loaded!");
        }
        return loaded;
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(CONFIG_PATH);
    }
}
