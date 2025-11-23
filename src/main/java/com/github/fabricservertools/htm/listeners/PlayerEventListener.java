package com.github.fabricservertools.htm.listeners;

import com.github.fabricservertools.htm.HTM;
import com.github.fabricservertools.htm.config.HTMConfig;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.Utility;
import com.github.fabricservertools.htm.api.LockableObject;
import com.github.fabricservertools.htm.events.PlayerPlaceBlockCallback;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.lock.type.PrivateLock;
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
import java.util.Optional;

public class PlayerEventListener {
    public static void init() {
        PlayerPlaceBlockCallback.EVENT.register(PlayerEventListener::onPlace);
        PlayerBlockBreakEvents.BEFORE.register(PlayerEventListener::onBeforeBreak);
        AttackBlockCallback.EVENT.register(PlayerEventListener::onAttackBlock);
    }

    private static InteractionResult onAttackBlock(Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (InteractionManager.pendingActions.containsKey(serverPlayer)) {
                InteractionManager.execute(serverPlayer.level().getServer(), serverPlayer, pos);

                world.updateNeighborsAt(pos, world.getBlockState(pos).getBlock(), null);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private static boolean onBeforeBreak(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (world.isClientSide()) {
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
                    Optional<LockableObject> unlocked = InteractionManager.getUnlockedLockable((ServerLevel) world, pos, blockEntity);
                    if (unlocked.isPresent()) {
                        unlocked.get().setLock(lock.get());
                        return true;
                    }
                }

                Utility.sendMessage(playerEntity, HTMTexts.CONTAINER_UNLOCKED);
                return true;
            }

            Utility.sendMessage(playerEntity, HTMTexts.NOT_OWNER);
            return false;
        }

        return true;
    }

    @SuppressWarnings({"ConstantConditions", "SameReturnValue"})
    private static InteractionResult onPlace(Player playerEntity, BlockPlaceContext context) {
        try {
            BlockPos pos = context.getClickedPos();
            Level world = context.getLevel();
            BlockState state = world.getBlockState(pos);

            if (world.isClientSide() || !state.hasBlockEntity()) {
                return InteractionResult.PASS;
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LockableObject) {
                if (HTMConfig.get().isAutoLocking(state)) {
                    if (InteractionManager.getLock((ServerLevel) world, pos, blockEntity).isPresent()) {
                        return InteractionResult.PASS;
                    }

                    ((LockableObject) blockEntity).setLock(new HTMContainerLock(PrivateLock.INSTANCE, (ServerPlayer) playerEntity));
                    Utility.sendMessage(playerEntity, HTMTexts.CONTAINER_SET.apply(PrivateLock.INSTANCE.displayName()));
                }
            }
        } catch (Exception e) {
            HTM.LOGGER.warn("Something went wrong auto locking", e);
        }

        return InteractionResult.PASS;
    }
}
