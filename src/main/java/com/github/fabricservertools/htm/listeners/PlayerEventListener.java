package com.github.fabricservertools.htm.listeners;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.LockType;
import com.github.fabricservertools.htm.api.LockableChestBlock;
import com.github.fabricservertools.htm.api.LockableObject;
import com.github.fabricservertools.htm.events.PlayerPlaceBlockCallback;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.impl.transfer.item.InventoryStorageImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import java.util.Optional;

public class PlayerEventListener {
    public static void init() {
        PlayerPlaceBlockCallback.EVENT.register(PlayerEventListener::onPlace);
        PlayerBlockBreakEvents.BEFORE.register(PlayerEventListener::onBeforeBreak);
        AttackBlockCallback.EVENT.register(PlayerEventListener::onAttackBlock);

    }

    private static ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if (world.isClient) return ActionResult.PASS;

        if (InteractionManager.pendingActions.containsKey((ServerPlayerEntity) player)) {
            InteractionManager.execute((ServerPlayerEntity) player, world, pos);

            world.updateNeighborsAlways(pos, world.getBlockState(pos).getBlock());
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private static boolean onBeforeBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (world.isClient) return true;
        ServerPlayerEntity playerEntity = (ServerPlayerEntity) player;

        if (blockEntity instanceof LockableObject) {
            HTMContainerLock lock = InteractionManager.getLock(playerEntity, pos);

            if (!lock.isLocked()) return true;

            if (lock.isOwner(playerEntity) || (HTM.config.canTrustedPlayersBreakChests && lock.canOpen(playerEntity))) {
                if (state.getBlock() instanceof LockableChestBlock) {
                    Optional<BlockEntity> unlocked = ((LockableChestBlock) state.getBlock()).getUnlockedPart(state, world, pos);
                    if (unlocked.isPresent()) {
                        BlockEntity unlockedBlockEntity = unlocked.get();
                        ((LockableObject) unlockedBlockEntity).setLock(lock);
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
                    if (InteractionManager.getLock((ServerWorld) world, blockEntity).isLocked())
                        return ActionResult.PASS;

                    HTMContainerLock lock = ((LockableObject) blockEntity).getLock();

                    lock.setType(LockType.PRIVATE_LOCK.build(), (ServerPlayerEntity) playerEntity);
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
