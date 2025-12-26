package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.api.Lock;
import com.github.fabricservertools.htm.command.HTMCommand;
import com.github.fabricservertools.htm.config.HTMConfig;
import com.github.fabricservertools.htm.interactions.InteractionManager;
import com.github.fabricservertools.htm.listeners.PlayerEventListener;
import com.github.fabricservertools.htm.listeners.LevelEventListener;
import eu.pb4.common.protection.api.CommonProtection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HTM implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("HTM");

	@Override
	public void onInitialize() {
        // For some reason, the Lock class has to be loaded before the HTMConfig class for the codecs to be initialised properly
        // Calling a method of the class loads it. Removing the line below results in an NPE when launching Minecraft
        Lock.bootstrap();
        HTMConfig.load();

        HTMCommand.bootstrap();
        CommandRegistrationCallback.EVENT.register(((dispatcher, buildContext, selection) -> HTMCommand.register(dispatcher)));
		CommonProtection.register(Identifier.fromNamespaceAndPath("htm", "containers"), new InteractionManager());

		PlayerEventListener.init();
		LevelEventListener.init();
	}
}
