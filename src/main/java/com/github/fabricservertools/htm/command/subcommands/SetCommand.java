package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.HTMComponents;
import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.command.suggestion.LockTypeSuggestionProvider;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.SetAction;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SetCommand implements SubCommand {

    @Override
    public void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(literal("set")
                .requires(Permissions.require("htm.command.set", true))
                .then(argument("type", StringArgumentType.word())
                        .suggests(new LockTypeSuggestionProvider())
                        .executes(this::set)
                )
        );
    }

    private int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Lock.Type type = Lock.Type.fromUiName(StringArgumentType.getString(context, "type"));
		if (type == null) {
			throw new SimpleCommandExceptionType(HTMComponents.INVALID_LOCK_TYPE).create();
		}

        ServerPlayer player = context.getSource().getPlayerOrException();
		InteractionManager.pendingActions.put(player, new SetAction(type.create(player)));
		context.getSource().sendSuccess(() -> HTMComponents.CLICK_TO_SELECT, false);
		return 0;
	}
}
