package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class QuietCommand implements SubCommand {

    @Override
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(literal("quiet")
                .requires(Permissions.require("htm.command.quiet", true))
                .executes(this::quiet)
        );
    }

    private int quiet(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();

		InteractionManager.toggleNoMessage(player);
		if (InteractionManager.noMessage.contains(player.getUUID())) {
			context.getSource().sendSuccess(() -> HTMComponents.TOGGLE_NO_MSG_ON, false);
            return 1;
		} else {
			context.getSource().sendSuccess(() -> HTMComponents.TOGGLE_NO_MSG_OFF, false);
            return 0;
		}
	}
}
