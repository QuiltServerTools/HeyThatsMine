package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableChestBlock;
import com.github.fabricservertools.htm.api.LockableObject;
import com.mojang.authlib.GameProfile;
import eu.pb4.common.protection.api.ProtectionProvider;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class InteractionManager implements ProtectionProvider {
    public static final Object2ObjectMap<ServerPlayerEntity, LockInteraction> pendingActions = new Object2ObjectOpenHashMap<>();
    public static final ObjectSet<UUID> persisting = new ObjectOpenHashSet<>();
    public static final ObjectSet<UUID> noMessage = new ObjectOpenHashSet<>();

    public static void execute(ServerPlayerEntity player, World world, BlockPos pos) {
        LockInteraction action = pendingActions.get(player);

        HTMContainerLock lock = getLock(player, pos);
        if (lock != null) action.execute(player, world, pos, getLock(player, pos));

        if (!persisting.contains(player.getUuid())) {
            pendingActions.remove(player);
        }
    }

    public static HTMContainerLock getLock(ServerPlayerEntity player, BlockPos pos) {
        HTMContainerLock lock = getLock(player.getWorld(), pos);
        if (lock == null) {
            player.sendMessage(Text.translatable("text.htm.error.unlockable"), false);
        }

        return lock;
    }

    public static HTMContainerLock getLock(ServerWorld world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) return null;

        return getLock(world, blockEntity);
    }

    public static HTMContainerLock getLock(ServerWorld world, BlockEntity blockEntity) {
        BlockState state = blockEntity.getCachedState();

        if (!(blockEntity instanceof LockableObject)) {
            return null;
        }

        HTMContainerLock lock = ((LockableObject) blockEntity).getLock();
        if (state.getBlock() instanceof LockableChestBlock) {
            lock = ((LockableChestBlock) state.getBlock()).getLockAt(state, world, blockEntity.getPos());
        }

        return lock;
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
        var lock = InteractionManager.getLock((ServerWorld) world, pos);
        return lock != null && lock.isLocked();
    }

    @Override
    public boolean canBreakBlock(World world, BlockPos pos, GameProfile profile, @Nullable PlayerEntity player) {
        var lock = InteractionManager.getLock((ServerWorld) world, pos);
        if (lock != null && lock.isLocked()) {
            return lock.getOwner().equals(profile.getId());
        }

        return true;
    }

    @Override
    public boolean isAreaProtected(World world, Box area) {
        return false;
    }
}
