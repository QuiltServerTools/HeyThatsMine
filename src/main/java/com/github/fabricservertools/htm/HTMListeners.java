package com.github.fabricservertools.htm;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;

public class HTMListeners {
    public static void init() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (blockEntity instanceof LockableContainerBlockEntity) {
                if (state.getBlock() instanceof ChestBlock) {
                    DoubleBlockProperties.Type type = ChestBlock.getDoubleBlockType(state);
                }

                HTMContainerLock lock = ((LockableObject) blockEntity).getLock();

                if (lock.getOwner() == null) {
                    return true;
                }

                if (lock.getOwner() != player.getUuid()) {
                    player.sendMessage(new TranslatableText("text.htm.error.not_owner"), false);
                    return false;
                }

                return true;
            }

            return true;
        });

        AttackBlockCallback.EVENT.register(((player, world, hand, pos, direction) -> {
            if (world.isClient) return ActionResult.PASS;

            if (InteractionManager.pendingActions.containsKey(player)) {
                InteractionManager.execute((ServerPlayerEntity) player, world, pos);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        }));
    }
}
