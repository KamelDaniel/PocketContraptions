package io.github.kameldaniel.screenhandler;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.ModBlocks;
import io.github.kameldaniel.block.block.PocketContraption;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.recipe.input.BlueprintBuildingInput;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class BlueprintBuildingTableScreenHandler extends AbstractBlueprintBuildingScreenHandler implements BlueprintBuildingInput {
    private final ScreenHandlerContext context;
    private final PlayerInventory playerInventory;

    private ItemStack blueprint = ItemStack.EMPTY;
    private ItemStack base = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;


    // Client Constructor
    public BlueprintBuildingTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    // Server Constructor
    public BlueprintBuildingTableScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModScreenHandlers.BLUEPRINT_BUILDING_TABLE, syncId);
        this.context = context;
        super.addBlueprintSlot(this, 44, 43);
        super.addBaseSlot(this, 62, 43);
        super.addResultSlot(this, 116, 43);
        super.addPlayerSlots(playerInventory, 8, 85);
        this.playerInventory = playerInventory;
    }

    @Override
    public void populateRecipeFinder(RecipeFinder finder) {}

    @Override
    public RecipeBookType getCategory() {
        return null;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        PocketContraptions.LOGGER.info("Running quickMove for player {} and slotIndex {}", player.getName(), slotIndex);
        if (slotIndex < 3 && super.getSlot(slotIndex).hasStack()) { // Stack is in BlueprintBuildingTable
            ItemStack stack = this.removeStack(slotIndex);
            if (super.insertItem(stack, 3, super.slots.size(), false)) return stack;
            else return ItemStack.EMPTY;
        } else { // Stack is in PlayerInventory
            ItemStack stack = super.slots.get(slotIndex).getStack();
            if (super.insertItem(stack, 0, 3, false)) return stack;
            else return ItemStack.EMPTY;
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.BLUEPRINT_BUILDING_TABLE);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this));
    }

    @Override
    public ItemStack getBlueprint() {
        return this.getStack(SlotIndex.BLUEPRINT.index());
    }

    @Override
    public ItemStack getBase() {
        return this.getStack(SlotIndex.BASE.index());
    }

    @Override
    public void setChecklist(BlueprintBuilding.Checklist checklist) {
        PocketContraptions.LOGGER.info("Running setChecklist for checklist {}", checklist);
        PocketContraptions.LOGGER.info("Blueprint: {}", this.getBlueprint());
        if (this.getBlueprint().isEmpty()) return;
        if (checklist == null)
            super.getSlot(SlotIndex.BLUEPRINT.index()).getStack().remove(ModComponents.BLUEPRINT_CHECKLIST);
        else
            super.getSlot(SlotIndex.BLUEPRINT.index()).getStack().set(ModComponents.BLUEPRINT_CHECKLIST, checklist);
    }

    @Override
    public List<Storage<ItemVariant>> getAvailableInventories() {
        return List.of(PlayerInventoryStorage.of(this.playerInventory));
    }

    @Override
    public ItemStack getStack(int slotIndex) {
        if (slotIndex == SlotIndex.BLUEPRINT.index()) return this.blueprint;
        if (slotIndex == SlotIndex.BASE.index()) return this.base;
        if (slotIndex == SlotIndex.RESULT.index()) return this.result;
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slotIndex, int amount) {
        ItemStack stack = this.getStack(slotIndex).split(amount);
        stack.remove(ModComponents.BLUEPRINT_CHECKLIST);
        return stack;
    }

    @Override
    public ItemStack removeStack(int slotIndex) {
        return this.removeStack(slotIndex, this.getStack(slotIndex).getCount());
    }

    @Override
    public void setStack(int slotIndex, ItemStack stack) {
        if (slotIndex == SlotIndex.BLUEPRINT.index()) this.blueprint = stack;
        if (slotIndex == SlotIndex.BASE.index()) this.base = stack;
        if  (slotIndex == SlotIndex.RESULT.index()) this.result = stack;
        this.markDirty();
    }

    @Override
    public void markDirty() {
        PocketContraptions.LOGGER.info("markDirty called");
        context.run((world, blockPos) -> {
            PocketContraptions.LOGGER.info("Entered context call");
            if (!world.isClient()) {
                ItemStack result = updateRecipe((ServerWorld) world, this);
                PocketContraptions.LOGGER.info("Crafting {}", result);
                if (!result.isEmpty() &&
                        // Item count is valid
                        result.getCount() + this.result.getCount() < this.getSlot(SlotIndex.RESULT.index()).getMaxItemCount(result) &&
                        // Item type is valid
                        this.result.isEmpty() || ItemStack.areItemsAndComponentsEqual(result, this.result)) {
                    PocketContraptions.LOGGER.info("Craft successful, result is now {}", this.result);
                    this.base.decrement(1);
                    if (this.result.isEmpty()) this.result = result;
                    else this.result.increment(result.getCount());
                    PocketContraptions.LOGGER.info("Crafted, result is now {} by adding {}", this.result, result);
                    super.onContentChanged(this);
                }
            }
        });
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return canUse(context, player, ModBlocks.BLUEPRINT_BUILDING_TABLE);
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {

    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        return this.getStack(slotIndex);
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public void clear() {
        this.blueprint = ItemStack.EMPTY;
        this.base = ItemStack.EMPTY;
        this.result = ItemStack.EMPTY;
    }
}
