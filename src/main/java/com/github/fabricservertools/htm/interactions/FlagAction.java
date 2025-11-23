package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.config.HTMConfig;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class FlagAction implements LockInteraction {

	/**
	 * Optional flag type and value to set it to.
	 * If empty, get flag info instead
	 */
	private final Optional<Pair<FlagType, Boolean>> flagSet;

	/**
	 * Creates a flag action
	 *
	 * @param flagSet Optional flag type and value to set it to.
	 *                If empty, get flag info instead
	 */
	public FlagAction(Optional<Pair<FlagType, Boolean>> flagSet) {
		this.flagSet = flagSet;
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayer player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.displayClientMessage(HTMComponents.NOT_OWNER, false);
			return;
		}

        BlockState state = player.level().getBlockState(pos);
		if (flagSet.isEmpty()) {
			//flag info
			player.displayClientMessage(HTMComponents.DIVIDER, false);
            lock.flags().forEach(state, (flag, value) -> {
                player.displayClientMessage(HTMComponents.CONTAINER_FLAG.apply(
                                flag.displayName(),
                                Component.literal(value.toString().toUpperCase()).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED, ChatFormatting.BOLD)),
                        false);
            });
			player.displayClientMessage(HTMComponents.DIVIDER, false);
		} else {
			//flag set
			FlagType flagType = flagSet.get().getFirst();
			Boolean value = flagSet.get().getSecond();

            HTMComponents.TranslatableComponentBuilder feedback;
            boolean feedbackValue;
            if (value == null) {
                object.setLock(lock.withoutFlag(flagType));
                feedback = HTMComponents.CONTAINER_FLAG_RESET;
                feedbackValue = HTMConfig.get().defaultFlags().get(flagType, state);
            } else {
                object.setLock(lock.withFlag(flagType, value));
                feedback = HTMComponents.CONTAINER_FLAG_SET;
                feedbackValue = value;
            }
            player.displayClientMessage(feedback.apply(
                            flagType.displayName(),
                            Component.literal(String.valueOf(feedbackValue).toUpperCase()).withStyle(feedbackValue ? ChatFormatting.GREEN : ChatFormatting.RED, ChatFormatting.BOLD)),
                    false);
        }
	}
}
