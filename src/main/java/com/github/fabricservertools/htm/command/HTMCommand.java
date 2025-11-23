package com.github.fabricservertools.htm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.literal;

public class HTMCommand {
	private static LiteralCommandNode<CommandSourceStack> rootNode;

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralCommandNode<CommandSourceStack> htmNode =
				literal("htm")
						.requires(Permissions.require("htm.command.root", true))
						.build();

		dispatcher.getRoot().addChild(htmNode);
		rootNode = htmNode;
	}

	public static void registerSubCommand(LiteralCommandNode<CommandSourceStack> subCommand) {
		rootNode.addChild(subCommand);
	}
}
