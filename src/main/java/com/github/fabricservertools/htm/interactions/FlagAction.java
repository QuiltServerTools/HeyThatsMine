package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.config.HTMConfig;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class FlagAction implements LockInteraction {

	/**
	 * Optional flag type and value to set it to.
	 * If empty, get flag info instead
	 */
	private final Optional<Tuple<FlagType, Boolean>> flagSet;

	/**
	 * Creates a flag action
	 *
	 * @param flagSet Optional flag type and value to set it to.
	 *                If empty, get flag info instead
	 */
	public FlagAction(Optional<Tuple<FlagType, Boolean>> flagSet) {
		this.flagSet = flagSet;
	}

	@Override
	public void execute(MinecraftServer server, ServerPlayer player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.displayClientMessage(HTMTexts.NOT_OWNER, false);
			return;
		}

        BlockState state = player.level().getBlockState(pos);
		if (flagSet.isEmpty()) {
			//flag info
			player.displayClientMessage(HTMTexts.DIVIDER, false);
            lock.flags().forEach(state, (flag, value) -> {
                player.displayClientMessage(HTMTexts.CONTAINER_FLAG.apply(
                                flag.displayName(),
                                Component.literal(value.toString().toUpperCase()).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED, ChatFormatting.BOLD)),
                        false);
            });
			player.displayClientMessage(HTMTexts.DIVIDER, false);
		} else {
			//flag set
			FlagType flagType = flagSet.get().getA();
			Boolean value = flagSet.get().getB();

            HTMTexts.TranslatableTextBuilder feedback;
            boolean feedbackValue;
            if (value == null) {
                object.setLock(lock.withoutFlag(flagType));
                feedback = HTMTexts.CONTAINER_FLAG_RESET;
                feedbackValue = HTMConfig.get().defaultFlags().get(flagType, state);
            } else {
                object.setLock(lock.withFlag(flagType, value));
                feedback = HTMTexts.CONTAINER_FLAG_SET;
                feedbackValue = value;
            }
            player.displayClientMessage(feedback.apply(
                            flagType.displayName(),
                            Component.literal(String.valueOf(feedbackValue).toUpperCase()).withStyle(feedbackValue ? ChatFormatting.GREEN : ChatFormatting.RED, ChatFormatting.BOLD)),
                    false);
        }
	}
}
