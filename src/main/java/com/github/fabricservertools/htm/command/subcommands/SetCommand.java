package com.github.fabricservertools.htm.command.subcommands;

import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.api.LockType;
import com.github.fabricservertools.htm.command.SubCommand;
import com.github.fabricservertools.htm.command.suggestors.LockTypeSuggestionProvider;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.interactions.SetAction;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetCommand implements SubCommand {
	@Override
	public LiteralCommandNode<ServerCommandSource> build() {
		return literal("set")
				.requires(Permissions.require("htm.command.set", true))
				.then(argument("type", StringArgumentType.word())
						.suggests(new LockTypeSuggestionProvider())
						.executes(this::set))
				.build();
	}

	private int set(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Lock type;
		ServerPlayerEntity player = context.getSource().getPlayer();

		type = LockType.lock(StringArgumentType.getString(context, "type").toLowerCase(), player);
		if (type == null) {
			throw new SimpleCommandExceptionType(Text.translatable("text.htm.error.lock_type")).create();
		}

		InteractionManager.pendingActions.put(player, new SetAction(type));
		context.getSource().sendFeedback(() -> Text.translatable("text.htm.select"), false);
		return 1;
	}
}
