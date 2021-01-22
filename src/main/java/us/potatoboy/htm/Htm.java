package us.potatoboy.htm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import us.potatoboy.htm.command.HTMCommand;

public class Htm implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            HTMCommand.register(dispatcher);
            HTMListeners.init();
        }));
    }
}
