package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.command.HTMCommand;
import com.github.fabricservertools.htm.command.subcommands.*;
import com.github.fabricservertools.htm.config.HTMConfig;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.listeners.PlayerEventListener;
import com.github.fabricservertools.htm.listeners.LevelEventListener;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.common.protection.api.CommonProtection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HTM implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("HTM");

	@Override
	public void onInitialize() {
        HTMConfig.load();

		CommandRegistrationCallback.EVENT.register(((dispatcher, environment, registryAccess) -> registerCommands(dispatcher)));
		CommonProtection.register(ResourceLocation.fromNamespaceAndPath("htm", "containers"), new InteractionManager());

		PlayerEventListener.init();
		LevelEventListener.init();
	}

	private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		HTMCommand.register(dispatcher);
		HTMCommand.registerSubCommand(new SetCommand().build());
		HTMCommand.registerSubCommand(new RemoveCommand().build());
		HTMCommand.registerSubCommand(new TrustCommand().build());
		HTMCommand.registerSubCommand(new UntrustCommand().build());
		HTMCommand.registerSubCommand(new InfoCommand().build());
		HTMCommand.registerSubCommand(new TransferCommand().build());
		HTMCommand.registerSubCommand(new FlagCommand().build());
		HTMCommand.registerSubCommand(new PersistCommand().build());
		HTMCommand.registerSubCommand(new QuietCommand().build());
	}
}
