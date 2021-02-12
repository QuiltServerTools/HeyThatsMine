package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.api.LockableChestBlock;
import com.github.fabricservertools.htm.api.LockableObject;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;

import java.util.Optional;

public class HTMListeners {
    public static void init() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) return true;
            ServerPlayerEntity playerEntity = (ServerPlayerEntity) player;

            if (blockEntity instanceof LockableContainerBlockEntity) {
                HTMContainerLock lock = InteractionManager.getLock(playerEntity, pos);

                if (!lock.isLocked()) return true;

                if (lock.isOwner((ServerPlayerEntity) player)) {
                    if (state.getBlock() instanceof LockableChestBlock) {
                        Optional<BlockEntity> unlocked = ((LockableChestBlock) state.getBlock()).getUnlockedPart(state, world, pos);
                        if (unlocked.isPresent()) {
                            BlockEntity unlockedBlockEntity = unlocked.get();
                            ((LockableObject)unlockedBlockEntity).setLock(lock);
                            return true;
                        }
                    }


                    player.sendMessage(new TranslatableText("text.htm.unlocked"), false);

                    return true;
                }

                return false;
            }

            return true;
        });

        AttackBlockCallback.EVENT.register(((player, world, hand, pos, direction) -> {
            if (world.isClient) return ActionResult.PASS;

            if (InteractionManager.pendingActions.containsKey(player)) {
                InteractionManager.execute((ServerPlayerEntity) player, world, pos);
                world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        }));
    }
}
