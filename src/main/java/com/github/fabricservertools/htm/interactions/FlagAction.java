package com.github.fabricservertools.htm.interactions;

import com.github.fabricservertools.htm.HTMContainerLock;
import com.github.fabricservertools.htm.api.FlagType;
import com.github.fabricservertools.htm.api.LockInteraction;
import com.github.fabricservertools.htm.api.LockableObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
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
			player.sendMessage(Text.translatable("text.htm.error.not_owner"), false);
			return;
		}

		if (flagSet.isEmpty()) {
			//flag info
			player.sendMessage(Text.translatable("text.htm.divider"), false);
			for (Map.Entry<FlagType, Boolean> entry : lock.flags().entrySet()) {
				player.sendMessage(Text.translatable(
								"text.htm.flag",
								entry.getKey().asString().toUpperCase(),
								Text.literal(entry.getValue().toString().toUpperCase()).formatted(entry.getValue() ? Formatting.GREEN : Formatting.RED, Formatting.BOLD)),
						false);
			}
			player.sendMessage(Text.translatable("text.htm.divider"), false);
		} else {
			//flag set
			FlagType flagType = flagSet.get().getLeft();
			boolean value = flagSet.get().getRight();
			object.setLock(lock.withFlag(flagType, value));
			player.sendMessage(Text.translatable(
					"text.htm.set_flag",
					flagType.asString().toUpperCase(),
					Text.literal(String.valueOf(value).toUpperCase()).formatted(value ? Formatting.GREEN : Formatting.RED, Formatting.BOLD)), false);
		}
	}
}
