package com.github.fabricservertools.htm.config;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.lock.FlagSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public record HTMConfig(boolean canTrustedPlayersBreakChests, FlagSet defaultFlags, List<Either<RegistryKey<Block>, TagKey<Block>>> autoLockingContainers) {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final Path CONFIG_PATH = Path.of("htm_config.json");

    private static final Codec<Either<RegistryKey<Block>, TagKey<Block>>> AUTO_LOCKING_CODEC = Codec.either(RegistryKey.createCodec(RegistryKeys.BLOCK), TagKey.codec(RegistryKeys.BLOCK));
    public static final Codec<HTMConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("can_trusted_players_break_chests").forGetter(HTMConfig::canTrustedPlayersBreakChests),
                    FlagSet.CONFIG_CODEC.fieldOf("default_flags").forGetter(HTMConfig::defaultFlags),
                    AUTO_LOCKING_CODEC.listOf().fieldOf("auto_locking_containers").forGetter(HTMConfig::autoLockingContainers)
            ).apply(instance, HTMConfig::new)
    );
    public static final Codec<HTMConfig> LEGACY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("canTrustedPlayersBreakChests").forGetter(HTMConfig::canTrustedPlayersBreakChests),
                    FlagSet.CONFIG_CODEC.fieldOf("defaultFlags").forGetter(HTMConfig::defaultFlags),
                    AUTO_LOCKING_CODEC.listOf().fieldOf("autolockingContainers").forGetter(HTMConfig::autoLockingContainers)
            ).apply(instance, HTMConfig::new)
    );
    public static final Codec<HTMConfig> SAFE_CODEC = Codec.withAlternative(CODEC, LEGACY_CODEC);

    private static final List<Either<RegistryKey<Block>, TagKey<Block>>> DEFAULT_AUTO_LOCKING_CONTAINERS = Stream.<Either<Block, TagKey<Block>>>of(
            Either.left(Blocks.CHEST),
            Either.left(Blocks.TRAPPED_CHEST),
            Either.left(Blocks.BARREL),
            Either.left(Blocks.FURNACE),
            Either.left(Blocks.BLAST_FURNACE),
            Either.left(Blocks.SMOKER),
            Either.right(BlockTags.SHULKER_BOXES),
            Either.right(BlockTags.COPPER_CHESTS))
            .map(either -> either.mapLeft(block -> block.getRegistryEntry().registryKey()))
            .toList();
    private static final HTMConfig DEFAULT_CONFIG = new HTMConfig(false, FlagSet.DEFAULT_FLAGS, DEFAULT_AUTO_LOCKING_CONTAINERS);

    private static HTMConfig loaded = null;

    public boolean isAutoLocking(Block block) {
        RegistryEntry<Block> entry = Registries.BLOCK.getEntry(block);
        RegistryKey<Block> key = entry.getKey().orElseThrow();
        for (Either<RegistryKey<Block>, TagKey<Block>> container : autoLockingContainers) {
            if (container.map(key::equals, entry::isIn)) {
                return true;
            }
        }
        return false;
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
