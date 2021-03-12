package com.github.fabricservertools.htm.mixin;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockableChestBlock;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;


@SuppressWarnings("ALL")
@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin extends AbstractChestBlock<ChestBlockEntity> implements LockableChestBlock {
	protected ChestBlockMixin(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
		super(settings, blockEntityTypeSupplier);
	}

	private final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, HTMContainerLock> LOCK_RETRIEVER =
			new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, HTMContainerLock>() {
				@Override
				public HTMContainerLock getFromBoth(ChestBlockEntity first, ChestBlockEntity second) {
					if (((LockableObject) first).getLock().isLocked()) {
						return ((LockableObject) first).getLock();
					}

					if (((LockableObject) second).getLock().isLocked()) {
						return ((LockableObject) second).getLock();
					}

					return ((LockableObject) first).getLock();
				}

				@Override
				public HTMContainerLock getFrom(ChestBlockEntity single) {
					return ((LockableObject) single).getLock();
				}

				@Override
				public HTMContainerLock getFallback() {
					return null;
				}
			};

	private final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<ChestBlockEntity>> UNLOCKED_RETRIEVER =
			new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<ChestBlockEntity>>() {
				@Override
				public Optional<ChestBlockEntity> getFromBoth(ChestBlockEntity first, ChestBlockEntity second) {
					if (!((LockableObject) first).getLock().isLocked()) {
						return Optional.of(first);
					}

					if (!((LockableObject) second).getLock().isLocked()) {
						return Optional.of(second);
					}

					return Optional.empty();
				}

				@Override
				public Optional<ChestBlockEntity> getFrom(ChestBlockEntity single) {
					return Optional.empty();
				}

				@Override
				public Optional<ChestBlockEntity> getFallback() {
					return Optional.empty();
				}
			};

	@Override
	public HTMContainerLock getLockAt(BlockState state, World world, BlockPos pos) {
		BiPredicate<WorldAccess, BlockPos> biPredicate2 = (worldAccess, blockPos) -> false;

		DoubleBlockProperties.PropertySource propertySource = DoubleBlockProperties.toPropertySource((BlockEntityType) this.entityTypeRetriever.get(), ChestBlock::getDoubleBlockType, ChestBlock::getFacing, ChestBlock.FACING, state, world, pos, biPredicate2);
		return (HTMContainerLock) propertySource.apply(LOCK_RETRIEVER);
	}

	@Override
	public Optional<BlockEntity> getUnlockedPart(BlockState state, World world, BlockPos pos) {
		BiPredicate<WorldAccess, BlockPos> biPredicate2 = (worldAccess, blockPos) -> false;

		DoubleBlockProperties.PropertySource propertySource = DoubleBlockProperties.toPropertySource((BlockEntityType) this.entityTypeRetriever.get(), ChestBlock::getDoubleBlockType, ChestBlock::getFacing, ChestBlock.FACING, state, world, pos, biPredicate2);
		return (Optional<BlockEntity>) propertySource.apply(UNLOCKED_RETRIEVER);
	}
}
