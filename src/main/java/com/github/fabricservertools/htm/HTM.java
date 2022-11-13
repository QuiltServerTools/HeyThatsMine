package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.api.LockType;
import com.github.fabricservertools.htm.command.HTMCommand;
import com.github.fabricservertools.htm.command.subcommands.*;
import com.github.fabricservertools.htm.config.HTMConfig;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.listeners.PlayerEventListener;
import com.github.fabricservertools.htm.listeners.WorldEventListener;
import com.mojang.brigadier.CommandDispatcher;
import eu.pb4.common.protection.api.CommonProtection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class HTM implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("HTM");
	public static HTMConfig config;

	@Override
	public void onInitialize() {
		LockType.init();
		registerFlags();

		config = HTMConfig.loadConfig(new File(FabricLoader.getInstance().getConfigDir() + "/htm_config.json"));

		CommandRegistrationCallback.EVENT.register(((dispatcher, environment, registryAccess) -> registerCommands(dispatcher)));
		CommonProtection.register(Identifier.of("htm", "containers"), new InteractionManager());

		PlayerEventListener.init();
		WorldEventListener.init();
	}

	private void registerFlags() {
		HTMRegistry.registerFlagType("hoppers");
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
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
