package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableChestBlock;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.BiPredicate;
import java.util.function.Supplier;


@SuppressWarnings("MixinInnerClass")
@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends AbstractChestBlock<ChestBlockEntity> implements LockableChestBlock {
    protected ChestBlockMixin(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    private final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, HTMContainerLock> LOCK_RETRIEVER =
            new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, HTMContainerLock>() {
                @Override
                public HTMContainerLock getFromBoth(ChestBlockEntity first, ChestBlockEntity second) {
                    if (((LockableObject) first).getLock() != null) {
                        return ((LockableObject) first).getLock();
                    }

                    if (((LockableObject) second).getLock() != null) {
                        return ((LockableObject) second).getLock();
                    }

                    return null;
                }

                @Override
                public HTMContainerLock getFrom(ChestBlockEntity single) {
                    if (((LockableObject) single).getLock() != null) {
                        return ((LockableObject) single).getLock();
                    }

                    return null;
                }

                @Override
                public HTMContainerLock getFallback() {
                    return null;
                }
            };

    @Override
    public HTMContainerLock getLockAt(BlockState state, World world, BlockPos pos) {
        BiPredicate<WorldAccess, BlockPos> biPredicate2 = (worldAccess, blockPos) -> false;
        ChestBlock chestblock = (ChestBlock)(Object)this;

        DoubleBlockProperties.PropertySource propertySource = DoubleBlockProperties.toPropertySource((BlockEntityType)this.entityTypeRetriever.get(), ChestBlock::getDoubleBlockType, ChestBlock::getFacing, chestblock.FACING, state, world, pos, biPredicate2);
        return (HTMContainerLock) propertySource.apply(LOCK_RETRIEVER);
    }
}
