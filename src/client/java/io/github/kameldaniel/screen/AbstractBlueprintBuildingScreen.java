package io.github.kameldaniel.screen;

import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import io.github.kameldaniel.screenhandler.AbstractBlueprintBuildingScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public abstract class AbstractBlueprintBuildingScreen<T extends AbstractBlueprintBuildingScreenHandler> extends HandledScreen<T> {
    public AbstractBlueprintBuildingScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    abstract BlueprintBuilding.Checklist getChecklist();
}