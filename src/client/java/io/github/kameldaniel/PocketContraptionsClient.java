package io.github.kameldaniel;

import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import io.github.kameldaniel.screen.BlueprintBuilderScreen;
import io.github.kameldaniel.screen.BlueprintBuildingTableScreen;
import io.github.kameldaniel.screenhandler.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.text.Text;

public class PocketContraptionsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreenHandlers.BLUEPRINT_BUILDER, BlueprintBuilderScreen::new);
		HandledScreens.register(ModScreenHandlers.BLUEPRINT_BUILDING_TABLE, BlueprintBuildingTableScreen::new);
		ItemTooltipCallback.EVENT.register((stack, context, type, tooltip) -> {
			if (stack.contains(ModComponents.DIM_KEY)) {
				tooltip.add(Text.translatable("tooltip.pocket-contraptions.dim_key", stack.get(ModComponents.DIM_KEY)));
			}
			if (stack.contains(ModComponents.BLUEPRINT_RECIPE)) {
				tooltip.add(Text.translatable(stack.getOrDefault(ModComponents.BLUEPRINT_RECIPE, BlueprintBuilding.EMPTY).craft(null, null).getItemName().getString() + " Blueprint"));
			}
			if (stack.contains(ModComponents.BLUEPRINT_CHECKLIST)) {
				tooltip.add(Text.translatable("Checklist:\nRequired: " + stack.getOrDefault(ModComponents.BLUEPRINT_CHECKLIST, BlueprintBuilding.Checklist.EMPTY).required().toString() + "\nAvailable: " + stack.getOrDefault(ModComponents.BLUEPRINT_CHECKLIST, BlueprintBuilding.Checklist.EMPTY).available().toString()));
			}
		});
	}
}