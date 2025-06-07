package com.github.fabricservertools.htm.listeners;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.LockableObject;
import com.github.fabricservertools.htm.events.PlayerPlaceBlockCallback;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.locks.PrivateLock;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public class PlayerEventListener {
    public static void init() {
        UseBlockCallback.EVENT.register(PlayerEventListener::onBlockUse);
        PlayerPlaceBlockCallback.EVENT.register(PlayerEventListener::onPlace);
        PlayerBlockBreakEvents.BEFORE.register(PlayerEventListener::onBeforeBreak);
        AttackBlockCallback.EVENT.register(PlayerEventListener::onAttackBlock);
    }

    private static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            Optional<HTMContainerLock> lock = InteractionManager.getLock(serverPlayer, hitResult.getBlockPos());
            if (lock.isEmpty() || lock.get().canOpen(serverPlayer)) {
                return ActionResult.PASS;
            }
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    private static ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (InteractionManager.pendingActions.containsKey(serverPlayer)) {
                InteractionManager.execute(serverPlayer.getServer(), serverPlayer, pos);

                world.updateNeighborsAlways(pos, world.getBlockState(pos).getBlock(), null);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    private static boolean onBeforeBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (world.isClient) return true;
        ServerPlayerEntity playerEntity = (ServerPlayerEntity) player;

        if (blockEntity instanceof LockableObject) {
            Optional<HTMContainerLock> lock = InteractionManager.getLock(playerEntity, pos);

            if (lock.isEmpty()) return true;

            if (lock.get().isOwner(playerEntity) || (HTM.config.canTrustedPlayersBreakChests && lock.get().canOpen(playerEntity))) {
                if (state.getBlock() instanceof ChestBlock) {
                    Optional<LockableObject> unlocked = InteractionManager.getUnlockedLockable((ServerWorld) world, pos, blockEntity);
                    if (unlocked.isPresent()) {
                        unlocked.get().setLock(lock.get());
                        return true;
                    }
                }

                Utility.sendMessage(playerEntity, Text.translatable("text.htm.unlocked"));

                return true;
            }

            Utility.sendMessage(playerEntity, Text.translatable("text.htm.error.not_owner"));
            return false;
        }

        return true;
    }

    @SuppressWarnings({"ConstantConditions", "SameReturnValue"})
    private static ActionResult onPlace(PlayerEntity playerEntity, ItemPlacementContext context) {
        try {
            BlockPos pos = context.getBlockPos();
            World world = context.getWorld();
            BlockState state = world.getBlockState(pos);

            if (world.isClient) return ActionResult.PASS;
            if (!state.hasBlockEntity()) return ActionResult.PASS;

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LockableObject) {
                if (HTM.config.autolockingContainers.contains(Registries.BLOCK.getId(state.getBlock()))) {
                    if (InteractionManager.getLock((ServerWorld) world, pos, blockEntity).isPresent())
                        return ActionResult.PASS;

                    ((LockableObject) blockEntity).setLock(new HTMContainerLock(PrivateLock.INSTANCE, (ServerPlayerEntity) playerEntity));
                    Utility.sendMessage(playerEntity, Text.translatable("text.htm.set", "PRIVATE"));
                }
            }
        } catch (Exception e) {
            HTM.LOGGER.warn("Something went wrong auto locking");
            e.printStackTrace();
        }

        return ActionResult.PASS;
    }
}
