package io.github.kameldaniel;

import io.github.kameldaniel.screen.BlueprintBuilderScreen;
import io.github.kameldaniel.screenhandler.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class PocketContraptionsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.BLUEPRINT_BUILDER, BlueprintBuilderScreen::new);
	}
}