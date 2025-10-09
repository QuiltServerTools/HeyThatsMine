package com.github.fabricservertools.htm.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public record SingleBlockSelector(Either<RegistryKey<Block>, TagKey<Block>> key) {
    public static final Codec<SingleBlockSelector> CODEC = Codec.either(RegistryKey.createCodec(RegistryKeys.BLOCK), TagKey.codec(RegistryKeys.BLOCK))
            .xmap(SingleBlockSelector::new, SingleBlockSelector::key);

    public SingleBlockSelector(Block block) {
        this(Either.left(block.getRegistryEntry().registryKey()));
    }

    public SingleBlockSelector(TagKey<Block> tag) {
        this(Either.right(tag));
    }

    public boolean is(BlockState block) {
        return is(Registries.BLOCK.getEntry(block.getBlock()));
    }

    public boolean is(RegistryEntry<Block> block) {
        RegistryKey<Block> registryKey = block.getKey().orElseThrow();
        return key.map(registryKey::equals, block::isIn);
    }
}
