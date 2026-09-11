package io.github.kameldaniel.recipe.input;

import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.input.RecipeInput;

import java.util.List;

public interface BlueprintBuildingInput extends RecipeInput, RecipeInputProvider, Inventory {

    ItemStack getBlueprint();

    ItemStack getBase();

    void setChecklist(BlueprintBuilding.Checklist checklist);

    List<Storage<ItemVariant>> getAvailableInventories();

    @Override
    default boolean isEmpty() {
        return getBlueprint().isEmpty() && getBase().isEmpty();
    }
}