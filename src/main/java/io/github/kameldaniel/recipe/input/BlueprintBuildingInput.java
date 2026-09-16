package io.github.kameldaniel.recipe.input;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.recipe.ModRecipes;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface BlueprintBuildingInput extends RecipeInput, RecipeInputProvider, Inventory {

    ItemStack getBlueprint();

    ItemStack getBase();

    void setChecklist(BlueprintBuilding.Checklist checklist);

    List<Storage<ItemVariant>> getAvailableInventories();

    @Override
    default boolean isEmpty() {
        return getBlueprint().isEmpty() && getBase().isEmpty();
    }

    /**
     * Gets a BlueprintBuilding recipe matching input
     * @param world
     * The ServerWorld
     * @return
     * null if there are no matching recipes, otherwise the first matching BlueprintBuilding recipe
     */
    private BlueprintBuilding getMatchingRecipe(ServerWorld world) {
        if (this.getBlueprint().isEmpty()) return null;
        if (this.getBlueprint().contains(ModComponents.BLUEPRINT_RECIPE)) {
            return this.getBlueprint().get(ModComponents.BLUEPRINT_RECIPE);
        } else {
            Optional<RecipeEntry<BlueprintBuilding>> entry = world.getRecipeManager().getFirstMatch(ModRecipes.BLUEPRINT_BUILDING, this, world);
            return entry.map(RecipeEntry::value).orElse(null);
        }
    }

    /**
     * Matches a BlueprintBuilding recipe and updates the Checklist.
     * This method will update the ExtractionPlan to extract all available ingredients,
     * but will run the extraction - That is the user's responsibility.
     * @param world
     * The ServerWorld
     * @param plan
     * The ExtractionPlan used to extract the ingredients; will be cleared first
     * @return
     * The recipe result ItemStack if the blueprint is correct, and the base
     *  and ingredients are successfully removed using the transaction,
     *  otherwise returns the empty ItemStack
     */
    default ItemStack updateRecipe(ServerWorld world, ExtractionPlan plan) {
        plan.clear();
        PocketContraptions.LOGGER.info("updateRecipe called.");
        if (this.getBlueprint().isEmpty()) {
            PocketContraptions.LOGGER.info("No blueprint, removing checklist");
            this.setChecklist(null);
            return ItemStack.EMPTY;
        }
        BlueprintBuilding recipe = this.getMatchingRecipe(world);
        if (recipe == null) {
            PocketContraptions.LOGGER.info("No matching recipe, removing checklist");
            this.setChecklist(null);
            return ItemStack.EMPTY;
        }
        // Found a matching recipe
        PocketContraptions.LOGGER.info("Recipe found! Checking Ingredients");
        BlueprintBuilding.Checklist checklist = recipe.getChecklist(plan, this);
        this.setChecklist(checklist);
        if (checklist.isComplete()) {
            PocketContraptions.LOGGER.info("All ingredients present, checking base");
            if (recipe.testBase(this.getBase())) {
                PocketContraptions.LOGGER.info("Base Matches, crafting");
                return recipe.craft(this, world.getRegistryManager());
            } else {
                PocketContraptions.LOGGER.info("Base Mismatch, returning");
                return ItemStack.EMPTY;
            }
        } else {
            PocketContraptions.LOGGER.info("Not all ingredients present, returning");
            return ItemStack.EMPTY;
        }
    }

    record Extraction(StorageView<ItemVariant> view, long amount) {
    }

    class ExtractionPlan extends ArrayList<Extraction> {
        public boolean add(StorageView<ItemVariant> view, long amount) {
            return super.add(new Extraction(view, amount));
        }

        /**
         * Checks that all Extractions in the List are possible
         * @return
         * true if all planned Extractions are possible, otherwise false
         */
        public boolean verify() {
            try (Transaction transaction = Transaction.openOuter()) {
                for (Extraction e : this) {
                    long actualAmount = e.view().extract(e.view().getResource(), e.amount(), transaction);
                    if (actualAmount != e.amount()) {
                        transaction.abort();
                        return false;
                    }
                }
                transaction.abort();
                return true;
            }
        }

        /**
         * Extracts each Extraction in the List
         * @return
         * true if all the Extractions are successful, false otherwise
         */
        public boolean extract() {
            try (Transaction transaction = Transaction.openOuter()) {
                for (Extraction e : this) {
                    long actualAmount = e.view().extract(e.view().getResource(), e.amount(), transaction);
                    if (actualAmount != e.amount()) {
                        transaction.abort();
                        return false;
                    }
                }
                transaction.commit();
                return true;
            }
        }
    }
}