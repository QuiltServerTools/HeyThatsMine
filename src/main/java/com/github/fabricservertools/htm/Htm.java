package com.github.fabricservertools.htm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import com.github.fabricservertools.htm.command.HTMCommand;

public class Htm implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            HTMCommand.register(dispatcher);
            HTMListeners.init();
        }));
    }
}
