package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.config.HTMConfig;
import com.github.fabricservertools.htm.lock.HTMContainerLock;
import com.github.fabricservertools.htm.HTMTexts;
import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

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
	public void execute(MinecraftServer server, ServerPlayerEntity player, BlockPos pos, LockableObject object, HTMContainerLock lock) {
		if (!lock.isOwner(player)) {
			player.sendMessage(HTMTexts.NOT_OWNER, false);
			return;
		}

		if (flagSet.isEmpty()) {
			//flag info
			player.sendMessage(HTMTexts.DIVIDER, false);
            lock.flags().forEach((flag, value) -> {
                player.sendMessage(HTMTexts.CONTAINER_FLAG.apply(
                                flag.displayName(),
                                Text.literal(value.toString().toUpperCase()).formatted(value ? Formatting.GREEN : Formatting.RED, Formatting.BOLD)),
                        false);
            });
			player.sendMessage(HTMTexts.DIVIDER, false);
		} else {
			//flag set
			FlagType flagType = flagSet.get().getLeft();
			Boolean value = flagSet.get().getRight();

            HTMTexts.TranslatableTextBuilder feedback;
            boolean feedbackValue;
            if (value == null) {
                object.setLock(lock.withoutFlag(flagType));
                feedback = HTMTexts.CONTAINER_FLAG_RESET;
                feedbackValue = HTMConfig.get().defaultFlags().get(flagType);
            } else {
                object.setLock(lock.withFlag(flagType, value));
                feedback = HTMTexts.CONTAINER_FLAG_SET;
                feedbackValue = value;
            }
            player.sendMessage(feedback.apply(
                            flagType.displayName(),
                            Text.literal(String.valueOf(feedbackValue).toUpperCase()).formatted(feedbackValue ? Formatting.GREEN : Formatting.RED, Formatting.BOLD)),
                    false);
        }
	}
}
