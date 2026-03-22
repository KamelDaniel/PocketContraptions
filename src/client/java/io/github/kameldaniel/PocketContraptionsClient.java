package io.github.kameldaniel;

import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.screen.BlueprintBuilderScreen;
import io.github.kameldaniel.screenhandler.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.text.Text;

public class PocketContraptionsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.BLUEPRINT_BUILDER, BlueprintBuilderScreen::new);
		ItemTooltipCallback.EVENT.register((stack, context, type, tooltip) -> {
			if (stack.contains(ModComponents.DIM_KEY))
				tooltip.add(Text.translatable("Attached Dim Key: " + stack.get(ModComponents.DIM_KEY)));
		});
	}
}