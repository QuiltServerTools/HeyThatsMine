package com.github.fabricservertools.htm;

import com.github.fabricservertools.htm.command.HTMCommand;
import com.github.fabricservertools.htm.config.HTMConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Htm implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static HTMConfig config;

    @Override
    public void onInitialize() {
        config = HTMConfig.loadConfig(new File(FabricLoader.getInstance().getConfigDir() + "/htm_config.json"));

        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            HTMCommand.register(dispatcher);
            HTMListeners.init();
        }));
    }
}
