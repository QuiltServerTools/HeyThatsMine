package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.api.LockableChestBlock;
import com.github.fabricservertools.htm.api.LockableObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InteractionManager {
    public static HashMap<ServerPlayerEntity, HTMInteractAction> pendingActions = new HashMap<>();

    public static void execute(ServerPlayerEntity player, World world, BlockPos pos) {
        HTMInteractAction action = pendingActions.get(player);
        switch (action.getType()) {
            case SET:
                set(action, player, world, pos);
                break;
            case TRUST:
                trust(action, player, world, pos);
                break;
            case REMOVE:
                remove(action, player, world, pos);
                break;
            case INFO:
                info(action, player, world, pos);
                break;
            case TRANSFER:
                transfer(action, player, world, pos);
                break;
            case FLAG:
                flag(action, player, world, pos);
                break;
        }

        pendingActions.remove(player);
    }

    private static void flag(HTMInteractAction action, ServerPlayerEntity player, World world, BlockPos pos) {
        HTMContainerLock lock = getLock(player, pos);
        if (lock == null) return;

        if (lock.getType() == null) {
            player.sendMessage(new TranslatableText("text.htm.error.no_lock"), false);
            return;
        }

        if (action.getFlagType() == null) {
            player.sendMessage(new TranslatableText("text.htm.divider"), false);
            for (Map.Entry<HTMContainerLock.FlagType, Boolean> entry : lock.getFlags().entrySet()) {
                player.sendMessage(new TranslatableText(
                        "text.htm.flag",
                        entry.getKey().name(),
                        new LiteralText(entry.getValue().toString().toUpperCase()).formatted(entry.getValue() ? Formatting.GREEN : Formatting.RED, Formatting.BOLD)),
                        false);
            }
            player.sendMessage(new TranslatableText("text.htm.divider"), false);
        } else {
            lock.setFlag(action.getFlagType(), action.getBool());
            player.sendMessage(new TranslatableText(
                    "text.htm.set_flag",
                    action.getFlagType().name().toUpperCase(),
                    new LiteralText(String.valueOf(action.getBool()).toUpperCase()).formatted(action.getBool() ? Formatting.GREEN : Formatting.RED, Formatting.BOLD)), false);
        }
    }

    private static void transfer(HTMInteractAction action, ServerPlayerEntity player, World world, BlockPos pos) {
        HTMContainerLock lock = getLock(player, pos);
        if (lock == null) return;

        if (lock.getType() == null) {
            player.sendMessage(new TranslatableText("text.htm.error.no_lock"), false);
            return;
        }

        if (!lock.isOwner(player)) return;

        if (lock.getOwner() == action.getTargetPlayer().getId()) {
            player.sendMessage(new TranslatableText("text.htm.error.trust_self"), false);
            return;
        }

        lock.transfer(action.getTargetPlayer().getId());
        player.sendMessage(new TranslatableText("text.htm.transfer", action.getTargetPlayer().getName()), false);
    }

    private static void info(HTMInteractAction action, ServerPlayerEntity player, World world, BlockPos pos) {
        HTMContainerLock lock = getLock(player, pos);
        if (lock == null) return;

        if (!lock.isLocked()) {
            player.sendMessage(new TranslatableText("text.htm.error.no_lock"), false);
            return;
        }

        GameProfile owner = world.getServer().getUserCache().getByUuid(lock.getOwner());

        player.sendMessage(new TranslatableText("text.htm.divider"), false);
        player.sendMessage(new TranslatableText("text.htm.type", lock.getType().name()), false);
        player.sendMessage(new TranslatableText("text.htm.owner", owner.getName()), false);
        if (owner.getId().equals(player.getUuid())) {
            String trustedList = lock.getTrusted()
                    .stream()
                    .map(a -> world.getServer().getUserCache().getByUuid(a).getName())
                    .collect(Collectors.joining(", "));

            player.sendMessage(new TranslatableText("text.htm.trusted", trustedList), false);
        }
        player.sendMessage(new TranslatableText("text.htm.divider"), false);
    }

    public static void trust(HTMInteractAction action, ServerPlayerEntity player, World world, BlockPos pos) {
        HTMContainerLock lock = getLock(player, pos);
        if (lock == null) return;

        if (lock.getType() == null) {
            player.sendMessage(new TranslatableText("text.htm.error.no_lock"), false);
            return;
        }

        if (!lock.isOwner(player)) return;

        if (lock.getOwner() == action.getTargetPlayer().getId()) {
            player.sendMessage(new TranslatableText("text.htm.error.trust_self"), false);
            return;
        }

        if (action.getBool()) {
            //untrust
            lock.removeTrust(action.getTargetPlayer().getId());
            player.sendMessage(new TranslatableText("text.htm.untrust", action.getTargetPlayer().getName()), false);
        } else {
            //trust
            if (lock.addTrust(action.getTargetPlayer().getId())){
                player.sendMessage(new TranslatableText("text.htm.trust", action.getTargetPlayer().getName()), false);
            } else {
                player.sendMessage(new TranslatableText("text.htm.error.already_trusted", action.getTargetPlayer().getName()), false);
            }
        }
    }

    public static void remove(HTMInteractAction action, ServerPlayerEntity player, World world, BlockPos pos) {
        HTMContainerLock lock = getLock(player, pos);
        if (lock == null) return;

        if (!lock.isLocked()) {
            player.sendMessage(new TranslatableText("text.htm.error.no_lock"), false);
            return;
        }

        if (!lock.isOwner(player)) return;

        lock.remove();
        player.sendMessage(new TranslatableText("text.htm.unlocked"), false);
    }

    public static void set(HTMInteractAction action, ServerPlayerEntity player, World world, BlockPos pos) {
        HTMContainerLock lock = getLock(player, pos);
        if (lock == null) return;

        if (lock.getType() != null) {
            player.sendMessage(new TranslatableText("text.htm.error.already_locked"), false);
            return;
        }

        lock.setType(action.getSetType(), player);
        player.sendMessage(new TranslatableText("text.htm.set", action.getSetType().name()), false);
    }

    public static HTMContainerLock getLock(ServerPlayerEntity player, BlockPos pos) {
        HTMContainerLock lock = getLock(player.getServerWorld(), pos);
        if (lock == null) {
            player.sendMessage(new TranslatableText("text.htm.error.unlockable"), false);
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

        if (!(blockEntity instanceof LockableContainerBlockEntity)) {
            return null;
        }

        HTMContainerLock lock = ((LockableObject) blockEntity).getLock();
        if (state.getBlock() instanceof LockableChestBlock) {
            lock = ((LockableChestBlock)state.getBlock()).getLockAt(state, world, blockEntity.getPos());
        }

        return lock;
    }
}
