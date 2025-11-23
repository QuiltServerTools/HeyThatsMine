package com.github.fabricservertools.htm.config;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record SingleBlockSelector(Either<ResourceKey<Block>, TagKey<Block>> key) {
    public static final Codec<SingleBlockSelector> CODEC = Codec.either(ResourceKey.codec(Registries.BLOCK), TagKey.hashedCodec(Registries.BLOCK))
            .xmap(SingleBlockSelector::new, SingleBlockSelector::key);

    public SingleBlockSelector(Block block) {
        this(Either.left(block.builtInRegistryHolder().key()));
    }

    public SingleBlockSelector(TagKey<Block> tag) {
        this(Either.right(tag));
    }

    public boolean is(BlockState block) {
        return is(BuiltInRegistries.BLOCK.wrapAsHolder(block.getBlock()));
    }

    public boolean is(Holder<Block> block) {
        ResourceKey<Block> registryKey = block.unwrapKey().orElseThrow();
        return key.map(registryKey::equals, block::is);
    }
}
