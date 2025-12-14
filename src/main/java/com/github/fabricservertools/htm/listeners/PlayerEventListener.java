package com.github.fabricservertools.htm.listeners;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.config.HTMConfig;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.LockableObject;
import com.github.fabricservertools.htm.events.PlayerPlaceBlockCallback;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class PlayerEventListener {
    public static void init() {
        PlayerPlaceBlockCallback.EVENT.register(PlayerEventListener::onPlace);
        PlayerBlockBreakEvents.BEFORE.register(PlayerEventListener::onBeforeBreak);
        AttackBlockCallback.EVENT.register(PlayerEventListener::onAttackBlock);
    }

    private static InteractionResult onAttackBlock(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (InteractionManager.pendingActions.containsKey(serverPlayer)) {
                InteractionManager.execute(serverPlayer.level().getServer(), serverPlayer, pos);

                level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock(), null);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private static boolean onBeforeBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (level.isClientSide()) {
            return true;
        }

        ServerPlayer playerEntity = (ServerPlayer) player;
        if (blockEntity instanceof LockableObject) {
            Optional<HTMContainerLock> lock = InteractionManager.getLock(playerEntity, pos);
            if (lock.isEmpty()) {
                return true;
            }

            if (lock.get().isOwner(playerEntity) || (HTMConfig.get().canTrustedPlayersBreakChests() && lock.get().canOpen(playerEntity))) {
                if (state.getBlock() instanceof ChestBlock) {
                    Optional<LockableObject> unlocked = InteractionManager.getUnlockedLockable((ServerLevel) level, pos, blockEntity);
                    if (unlocked.isPresent()) {
                        unlocked.get().setLock(lock.get());
                        return true;
                    }
                }

                Utility.sendMessage(playerEntity, HTMComponents.CONTAINER_UNLOCKED);
                return true;
            }

            Utility.sendMessage(playerEntity, HTMComponents.NOT_OWNER);
            return false;
        }

        return true;
    }

    @SuppressWarnings({"ConstantConditions", "SameReturnValue"})
    private static InteractionResult onPlace(Player player, BlockPlaceContext context) {
        try {
            BlockPos pos = context.getClickedPos();
            Level world = context.getLevel();
            BlockState state = world.getBlockState(pos);

            if (!(player instanceof ServerPlayer serverPlayer) || !state.hasBlockEntity()) {
                return InteractionResult.PASS;
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LockableObject) {
                Optional<Lock.Type> autoLockingType = HTMConfig.get().getAutoLockingType(state);
                if (autoLockingType.isPresent()) {
                    if (InteractionManager.getLock((ServerLevel) world, pos, blockEntity).isPresent()) {
                        return InteractionResult.PASS;
                    }

                    ((LockableObject) blockEntity).setLock(new HTMContainerLock(autoLockingType.get().create(serverPlayer), serverPlayer));
                    Utility.sendMessage(player, HTMComponents.CONTAINER_SET.apply(autoLockingType.get().displayName()));
                }
            }
        } catch (Exception e) {
            HTM.LOGGER.warn("Something went wrong auto locking", e);
        }

        return InteractionResult.PASS;
    }
}
