package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.HTMPermissions;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.permission.v1.PermissionPredicates;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class PersistCommand implements SubCommand {

    @Override
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(literal("persist")
                .requires(PermissionPredicates.require(HTMPermissions.COMMAND_PERSIST, true))
                .executes(this::persist)
        );
    }

    private int persist(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();

		InteractionManager.togglePersist(player);
		if (InteractionManager.persisting.contains(player.getUUID())) {
			context.getSource().sendSuccess(() -> HTMComponents.TOGGLE_PERSIST_ON, false);
            return 1;
		} else {
			context.getSource().sendSuccess(() -> HTMComponents.TOGGLE_PERSIST_OFF, false);
            return 0;
		}
	}
}
