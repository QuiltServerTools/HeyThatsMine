package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import com.mojang.authlib.GameProfile;
import eu.pb4.common.protection.api.ProtectionProvider;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class InteractionManager implements ProtectionProvider {
    public static final Object2ObjectMap<ServerPlayerEntity, LockInteraction> pendingActions = new Object2ObjectOpenHashMap<>();
    public static final ObjectSet<UUID> persisting = new ObjectOpenHashSet<>();
    public static final ObjectSet<UUID> noMessage = new ObjectOpenHashSet<>();

    private static final DoubleBlockProperties.PropertyRetriever<BlockEntity, LockableObject> LOCKABLE_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<>() {
        @Override
        public LockableObject getFromBoth(BlockEntity first, BlockEntity second) {
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
        public LockableObject getFrom(BlockEntity single) {
            if (single instanceof LockableObject lockable) {
                return lockable;
            }
            return null;
        }

        @Override
        public LockableObject getFallback() {
            return null;
        }
    };

    private static final DoubleBlockProperties.PropertyRetriever<BlockEntity, LockableObject> UNLOCKED_LOCKABLE_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<>() {
        @Override
        public LockableObject getFromBoth(BlockEntity first, BlockEntity second) {
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
        public LockableObject getFrom(BlockEntity single) {
            if (single instanceof LockableObject lockable && lockable.getLock().isEmpty()) {
                return lockable;
            }
            return null;
        }

        @Override
        public LockableObject getFallback() {
            return null;
        }
    };

    public static void execute(MinecraftServer server, ServerPlayerEntity player, BlockPos pos) {
        LockInteraction action = pendingActions.get(player);

        Optional<LockableObject> lockableObject = getLockable(player, pos);
        lockableObject.ifPresentOrElse(object -> {
            Optional<HTMContainerLock> containerLock = object.getLock();
            if (action.requiresLock()) {
                containerLock.ifPresentOrElse(
                        lock -> action.execute(server, player, pos, object, lock),
                        () -> player.sendMessage(Text.translatable("text.htm.error.no_lock"), false));
            } else {
                action.execute(server, player, pos, object, containerLock.orElse(null));
            }
        }, () -> player.sendMessage(Text.translatable("text.htm.error.unlockable"), false));

        if (!persisting.contains(player.getUuid())) {
            pendingActions.remove(player);
        }
    }

    public static Optional<HTMContainerLock> getLock(ServerPlayerEntity player, BlockPos pos) {
        return getLockable(player, pos).flatMap(LockableObject::getLock);
    }

    public static Optional<LockableObject> getLockable(ServerPlayerEntity player, BlockPos pos) {
        return getLockable(player.getWorld(), pos);
    }

    public static Optional<HTMContainerLock> getLock(ServerWorld world, BlockPos pos) {
        return getLockable(world, pos).flatMap(LockableObject::getLock);
    }

    public static Optional<LockableObject> getLockable(ServerWorld world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return Optional.empty();
        }

        return getLockable(world, pos, blockEntity);
    }

    public static Optional<HTMContainerLock> getLock(ServerWorld world, BlockPos pos, BlockEntity blockEntity) {
        return getLockable(world, pos, blockEntity).flatMap(LockableObject::getLock);
    }

    public static Optional<LockableObject> getLockable(ServerWorld world, BlockPos pos, BlockEntity blockEntity) {
        if (blockEntity instanceof ChestBlockEntity chest) {
            DoubleBlockProperties.PropertySource<? extends BlockEntity> propertySource = DoubleBlockProperties.toPropertySource(chest.getType(), ChestBlock::getDoubleBlockType,
                    ChestBlock::getFacing, ChestBlock.FACING, world.getBlockState(pos), world, pos, (access, blockPos) -> false);
            return Optional.ofNullable(propertySource.apply(LOCKABLE_RETRIEVER));
        } else if (blockEntity instanceof LockableObject lockable) {
            return Optional.of(lockable);
        }
        return Optional.empty();
    }

    public static Optional<LockableObject> getUnlockedLockable(ServerWorld world, BlockPos pos, BlockEntity blockEntity) {
        if (blockEntity instanceof ChestBlockEntity chest) {
            DoubleBlockProperties.PropertySource<? extends BlockEntity> propertySource = DoubleBlockProperties.toPropertySource(chest.getType(), ChestBlock::getDoubleBlockType,
                    ChestBlock::getFacing, ChestBlock.FACING, world.getBlockState(pos), world, pos, (access, blockPos) -> false);
            return Optional.ofNullable(propertySource.apply(UNLOCKED_LOCKABLE_RETRIEVER));
        } else if (blockEntity instanceof LockableObject lockable && lockable.getLock().isEmpty()) {
            return Optional.of(lockable);
        }
        return Optional.empty();
    }

    public static void togglePersist(ServerPlayerEntity player) {
        if (persisting.contains(player.getUuid())) {
            persisting.remove(player.getUuid());
        } else {
            persisting.add(player.getUuid());
        }
    }

    public static void toggleNoMessage(ServerPlayerEntity player) {
        if (noMessage.contains(player.getUuid())) {
            noMessage.remove(player.getUuid());
        } else {
            noMessage.add(player.getUuid());
        }
    }

    @Override
    public boolean isProtected(World world, BlockPos pos) {
        var lock = InteractionManager.getLockable((ServerWorld) world, pos);
        return lock.isPresent();
    }

    @Override
    public boolean canBreakBlock(World world, BlockPos pos, GameProfile profile, @Nullable PlayerEntity player) {
        var lockable = InteractionManager.getLockable((ServerWorld) world, pos);
        return lockable.flatMap(LockableObject::getLock).map(htmContainerLock -> htmContainerLock.owner().equals(profile.getId())).orElse(true);
    }

    @Override
    public boolean isAreaProtected(World world, Box area) {
        return false;
    }
}
