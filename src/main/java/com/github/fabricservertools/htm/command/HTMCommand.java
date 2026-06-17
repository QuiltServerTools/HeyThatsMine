package com.github.fabricservertools.htm.command;

import com.github.fabricservertools.htm.HTMPermissions;
import com.github.fabricservertools.htm.command.subcommands.FlagCommand;
import com.github.fabricservertools.htm.command.subcommands.InfoCommand;
import com.github.fabricservertools.htm.command.subcommands.PersistCommand;
import com.github.fabricservertools.htm.command.subcommands.QuietCommand;
import com.github.fabricservertools.htm.command.subcommands.RemoveCommand;
import com.github.fabricservertools.htm.command.subcommands.SetCommand;
import com.github.fabricservertools.htm.command.subcommands.TransferCommand;
import com.github.fabricservertools.htm.command.subcommands.TrustCommand;
import com.github.fabricservertools.htm.command.subcommands.UntrustCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.permission.v1.PermissionPredicates;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.commands.Commands.literal;

public class HTMCommand {
    private static final List<SubCommand> SUB_COMMANDS = new ArrayList<>();
    private static boolean registeredCommand = false;

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> htmNode = literal("htm")
                .requires(PermissionPredicates.require(HTMPermissions.COMMAND_ROOT, true));
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
    
    public static void bootstrap() {
        registerSubCommand(new SetCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new TrustCommand());
        registerSubCommand(new UntrustCommand());
        registerSubCommand(new InfoCommand());
        registerSubCommand(new TransferCommand());
        registerSubCommand(new FlagCommand());
        registerSubCommand(new PersistCommand());
        registerSubCommand(new QuietCommand());
    }
}
