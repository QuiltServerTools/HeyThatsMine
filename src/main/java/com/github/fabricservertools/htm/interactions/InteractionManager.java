package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import com.mojang.authlib.GameProfile;
import eu.pb4.common.protection.api.ProtectionProvider;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class InteractionManager implements ProtectionProvider {
    public static final Object2ObjectMap<ServerPlayer, LockInteraction> pendingActions = new Object2ObjectOpenHashMap<>();
    public static final ObjectSet<UUID> persisting = new ObjectOpenHashSet<>();
    public static final ObjectSet<UUID> noMessage = new ObjectOpenHashSet<>();

    private static final DoubleBlockCombiner.Combiner<BlockEntity, LockableObject> LOCKABLE_RETRIEVER = new DoubleBlockCombiner.Combiner<>() {

        @Override
        public @Nullable LockableObject acceptDouble(BlockEntity first, BlockEntity second) {
            if (first instanceof LockableObject lockable) {
                if (lockable.getLock().isPresent()) {
                  return lockable;
                } else if (second instanceof LockableObject secondLockable && secondLockable.getLock().isPresent()) {
                    return secondLockable;
                }
                return lockable;
            }
            return null;
        }

        @Override
        public @Nullable LockableObject acceptSingle(BlockEntity single) {
            if (single instanceof LockableObject lockable) {
                return lockable;
            }
            return null;
        }

        @Override
        public @Nullable LockableObject acceptNone() {
            return null;
        }
    };

    private static final DoubleBlockCombiner.Combiner<BlockEntity, LockableObject> UNLOCKED_LOCKABLE_RETRIEVER = new DoubleBlockCombiner.Combiner<>() {
        @Override
        public @Nullable LockableObject acceptDouble(BlockEntity first, BlockEntity second) {
            if (first instanceof LockableObject lockable) {
                if (lockable.getLock().isEmpty()) {
                    return lockable;
                } else if (second instanceof LockableObject secondLockable && secondLockable.getLock().isEmpty()) {
                    return secondLockable;
                }
            }
            return null;
        }

        @Override
        public @Nullable LockableObject acceptSingle(BlockEntity single) {
            if (single instanceof LockableObject lockable && lockable.getLock().isEmpty()) {
                return lockable;
            }
            return null;
        }

        @Override
        public @Nullable LockableObject acceptNone() {
            return null;
        }
    };

    public static void execute(MinecraftServer server, ServerPlayer player, BlockPos pos) {
        LockInteraction action = pendingActions.get(player);

        Optional<LockableObject> lockableObject = getLockable(player, pos);
        lockableObject.ifPresentOrElse(object -> {
            Optional<HTMContainerLock> containerLock = object.getLock();
            if (action.requiresLock()) {
                containerLock.ifPresentOrElse(
                        lock -> action.execute(server, player, pos, object, lock),
                        () -> player.displayClientMessage(HTMComponents.NOT_LOCKED, false));
            } else {
                //noinspection DataFlowIssue - if requiresLock is false then action should be able to accept null lock
                action.execute(server, player, pos, object, containerLock.orElse(null));
            }
        }, () -> player.displayClientMessage(HTMComponents.NOT_LOCKABLE, false));

        if (!persisting.contains(player.getUUID())) {
            pendingActions.remove(player);
        }
    }

    public static boolean canOpen(ServerPlayer player, BlockPos pos) {
        return getLock(player, pos).map(lock -> lock.canOpen(player)).orElse(true);
    }

    public static Optional<HTMContainerLock> getLock(ServerPlayer player, BlockPos pos) {
        return getLockable(player, pos).flatMap(LockableObject::getLock);
    }

    public static Optional<LockableObject> getLockable(ServerPlayer player, BlockPos pos) {
        return getLockable(player.level(), pos);
    }

    public static Optional<HTMContainerLock> getLock(ServerLevel world, BlockPos pos) {
        return getLockable(world, pos).flatMap(LockableObject::getLock);
    }

    public static Optional<LockableObject> getLockable(ServerLevel world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return Optional.empty();
        }

        return getLockable(world, pos, blockEntity);
    }

    public static Optional<HTMContainerLock> getLock(ServerLevel world, BlockPos pos, BlockEntity blockEntity) {
        return getLockable(world, pos, blockEntity).flatMap(LockableObject::getLock);
    }

    public static Optional<LockableObject> getLockable(ServerLevel world, BlockPos pos, BlockEntity blockEntity) {
        if (blockEntity instanceof ChestBlockEntity chest) {
            DoubleBlockCombiner.NeighborCombineResult<? extends BlockEntity> propertySource = DoubleBlockCombiner.combineWithNeigbour(chest.getType(), ChestBlock::getBlockType,
                    ChestBlock::getConnectedDirection, ChestBlock.FACING, world.getBlockState(pos), world, pos, (access, blockPos) -> false);
            //noinspection OptionalOfNullableMisuse - LOCKABLE_RETRIEVER may return null
            return Optional.ofNullable(propertySource.apply(LOCKABLE_RETRIEVER));
        } else if (blockEntity instanceof LockableObject lockable) {
            return Optional.of(lockable);
        }
        return Optional.empty();
    }

    public static Optional<LockableObject> getUnlockedLockable(ServerLevel world, BlockPos pos, BlockEntity blockEntity) {
        if (blockEntity instanceof ChestBlockEntity chest) {
            DoubleBlockCombiner.NeighborCombineResult<? extends BlockEntity> propertySource = DoubleBlockCombiner.combineWithNeigbour(chest.getType(), ChestBlock::getBlockType,
                    ChestBlock::getConnectedDirection, ChestBlock.FACING, world.getBlockState(pos), world, pos, (access, blockPos) -> false);
            //noinspection OptionalOfNullableMisuse - UNLOCKED_LOCKABLE_RETRIEVER may return null
            return Optional.ofNullable(propertySource.apply(UNLOCKED_LOCKABLE_RETRIEVER));
        } else if (blockEntity instanceof LockableObject lockable && lockable.getLock().isEmpty()) {
            return Optional.of(lockable);
        }
        return Optional.empty();
    }

    public static void togglePersist(ServerPlayer player) {
        if (persisting.contains(player.getUUID())) {
            persisting.remove(player.getUUID());
        } else {
            persisting.add(player.getUUID());
        }
    }

    public static void toggleNoMessage(ServerPlayer player) {
        if (noMessage.contains(player.getUUID())) {
            noMessage.remove(player.getUUID());
        } else {
            noMessage.add(player.getUUID());
        }
    }

    @Override
    public boolean isProtected(Level world, BlockPos pos) {
        var lock = InteractionManager.getLockable((ServerLevel) world, pos);
        return lock.isPresent();
    }

    @Override
    public boolean canBreakBlock(Level world, BlockPos pos, GameProfile profile, @Nullable Player player) {
        var lockable = InteractionManager.getLockable((ServerLevel) world, pos);
        return lockable.flatMap(LockableObject::getLock).map(lock -> lock.owner().equals(profile.id())).orElse(true);
    }

    @Override
    public boolean isAreaProtected(Level world, AABB area) {
        return false;
    }
}
