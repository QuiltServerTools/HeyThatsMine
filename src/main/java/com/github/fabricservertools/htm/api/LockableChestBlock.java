package com.github.fabricservertools.htm.api;

import com.github.fabricservertools.htm.HTMContainerLock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface LockableChestBlock {
    HTMContainerLock getLockAt(final BlockState state, final World world, final BlockPos pos);
}
