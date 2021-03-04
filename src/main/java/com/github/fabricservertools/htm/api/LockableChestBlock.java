package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTMContainerLock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public interface LockableChestBlock {
	HTMContainerLock getLockAt(final BlockState state, final World world, final BlockPos pos);

	Optional<BlockEntity> getUnlockedPart(final BlockState state, final World world, final BlockPos pos);
}
