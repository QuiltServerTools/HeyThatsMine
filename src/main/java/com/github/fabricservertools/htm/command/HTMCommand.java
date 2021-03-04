package com.github.fabricservertools.htm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class HTMCommand {
	private static LiteralCommandNode<ServerCommandSource> rootNode;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralCommandNode<ServerCommandSource> htmNode =
				literal("htm")
						.requires(Permissions.require("htm.command.root", true))
						.build();

		dispatcher.getRoot().addChild(htmNode);
		rootNode = htmNode;
	}

	public static void registerSubCommand(LiteralCommandNode<ServerCommandSource> subCommand) {
		rootNode.addChild(subCommand);
	}
}
