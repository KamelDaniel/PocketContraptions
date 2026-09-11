package io.github.kameldaniel.screenhandler;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.block.PocketContraption;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.recipe.ModRecipes;
import io.github.kameldaniel.recipe.input.BlueprintBuildingInput;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public abstract class AbstractBlueprintBuildingScreenHandler extends AbstractRecipeScreenHandler {

    protected AbstractBlueprintBuildingScreenHandler(
            ScreenHandlerType<? extends AbstractBlueprintBuildingScreenHandler> screenHandlerType,
            int syncId) {
        super(screenHandlerType, syncId);

    }

    protected Slot addBlueprintSlot(Inventory inventory, int x, int y) {
        return this.addSlot(new Slot(inventory, SlotIndex.BLUEPRINT.index(), x, y));
    }

    protected Slot addBaseSlot(Inventory inventory, int x, int y) {
        return this.addSlot(new Slot(inventory, SlotIndex.BASE.index(), x, y) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
    }

    protected Slot addResultSlot(Inventory inventory, int x, int y) {
        return this.addSlot(new Slot(inventory, SlotIndex.RESULT.index(), x, y) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack getStack() {
                ItemStack result = super.getStack();
//                PocketContraptions.LOGGER.info("Got ResultSlot: {}", result);
                return result;
            }
        });
    }

    public static ItemStack updateRecipe(ServerWorld world, BlueprintBuildingInput input) {
        PocketContraptions.LOGGER.info("updateRecipe called.");
        if (input.getBlueprint().isEmpty()) {
            PocketContraptions.LOGGER.info("No blueprint, removing checklist");
            input.setChecklist(null);
            return ItemStack.EMPTY;
        }
        BlueprintBuilding recipe = getMatchingRecipe(world, input);
        if (recipe == null) {
            PocketContraptions.LOGGER.info("No matching recipe, removing checklist");
            input.setChecklist(null);
            return ItemStack.EMPTY;
        }
        // Found a matching recipe
        try (Transaction transaction = Transaction.openOuter()) {
            PocketContraptions.LOGGER.info("Recipe found! Checking Ingredients");
            BlueprintBuilding.Checklist checklist = recipe.getChecklist(transaction, input);
            input.setChecklist(checklist);
            if (checklist.isComplete()) {
                PocketContraptions.LOGGER.info("All ingredients present, checking base");
                if (recipe.testBase(input.getBase())) {
                    PocketContraptions.LOGGER.info("Base Matches, crafting");
                    transaction.commit();
                    ItemStack result = recipe.craft(input, world.getRegistryManager());
                    return result;
                } else {
                    PocketContraptions.LOGGER.info("Base Mismatch, returning");
                    return ItemStack.EMPTY;
                }
            } else {
                PocketContraptions.LOGGER.info("Not all ingredients present, returning");
                return ItemStack.EMPTY;
            }
        }
    }

    private static BlueprintBuilding getMatchingRecipe(ServerWorld world, BlueprintBuildingInput input) {
        if (input.getBlueprint().isEmpty()) return null;
        if (input.getBlueprint().contains(ModComponents.BLUEPRINT_RECIPE)) input.getBlueprint().get(ModComponents.BLUEPRINT_RECIPE);
        Optional<RecipeEntry<BlueprintBuilding>> entry = world.getRecipeManager().getFirstMatch(ModRecipes.BLUEPRINT_BUILDING, input, world);
        return entry.map(RecipeEntry::value).orElse(null);
    }

    @Override
    public PostFillAction fillInputSlots(boolean craftAll, boolean creative, RecipeEntry<?> recipeEntry, ServerWorld world, PlayerInventory inventory) {
        return PostFillAction.NOTHING;
    }

    @Override
    public abstract void populateRecipeFinder(RecipeFinder finder);

    @Override
    public abstract RecipeBookType getCategory();

    @Override
    public abstract ItemStack quickMove(PlayerEntity player, int slot);

    @Override
    public abstract boolean canUse(PlayerEntity player);

    protected enum SlotIndex {
        BLUEPRINT (0),
        BASE (1),
        RESULT (2);

        private final int index;

        SlotIndex(int index) {
            this.index = index;
        }

        public int index() {
            return index;
        }
    }
}
