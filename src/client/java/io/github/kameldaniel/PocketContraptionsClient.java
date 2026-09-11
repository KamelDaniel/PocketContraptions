package io.github.kameldaniel;

import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.recipe.ModRecipes;
import io.github.kameldaniel.screen.BlueprintBuilderScreen;
import io.github.kameldaniel.screen.BlueprintBuildingTableScreen;
import io.github.kameldaniel.screenhandler.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class PocketContraptionsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.BLUEPRINT_BUILDER, BlueprintBuilderScreen::new);
		HandledScreens.register(ModScreenHandlers.BLUEPRINT_BUILDING_TABLE, BlueprintBuildingTableScreen::new);
		ItemTooltipCallback.EVENT.register((stack, context, type, tooltip) -> {
			if (stack.contains(ModComponents.DIM_KEY))
				tooltip.add(Text.translatable("Attached Dim Key: " + stack.get(ModComponents.DIM_KEY)));
			if (stack.contains(ModComponents.BLUEPRINT_RECIPE))
				tooltip.add(Text.translatable(stack.get(ModComponents.BLUEPRINT_RECIPE).craft(null, null).getItemName().getLiteralString() + " Blueprint"));
			if (stack.contains(ModComponents.BLUEPRINT_CHECKLIST))
				tooltip.add(Text.translatable("Checklist:\nRequired: " + stack.get(ModComponents.BLUEPRINT_CHECKLIST).required().toString() + "\nAvailable: " + stack.get(ModComponents.BLUEPRINT_CHECKLIST).available().toString()));
		});
	}
}