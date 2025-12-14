package com.github.fabricservertools.htm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class HTMCommand {
    private static final List<SubCommand> SUB_COMMANDS = new ArrayList<>();
    private static boolean registeredCommand = false;

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> htmNode = literal("htm")
                .requires(Permissions.require("htm.command.root", true));
        SUB_COMMANDS.forEach(command -> command.register(htmNode));
        dispatcher.register(htmNode);

        registeredCommand = true;
	}

	public static void registerSubCommand(SubCommand subCommand) {
        if (registeredCommand) {
            throw new IllegalStateException("Tried to register a sub-command after /htm was registered!");
        }
        SUB_COMMANDS.add(subCommand);
	}
}
