package io.github.haykam821.profilelookup;

import io.github.haykam821.profilelookup.command.ProfileCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			ProfileCommand.register(dispatcher);
		});
	}
}
