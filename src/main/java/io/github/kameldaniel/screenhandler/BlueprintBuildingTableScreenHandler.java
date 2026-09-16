package io.github.kameldaniel.screenhandler;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.ModBlocks;
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
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class BlueprintBuildingTableScreenHandler extends AbstractBlueprintBuildingScreenHandler implements BlueprintBuildingInput {
    private final ScreenHandlerContext context;
    private final PlayerInventory playerInventory;
    private final BlueprintBuildingInput.ExtractionPlan plan = new BlueprintBuildingInput.ExtractionPlan();

    private ItemStack blueprint = ItemStack.EMPTY;
    private ItemStack base = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;


    /**
     * Client Constructor
     * - Creates a BlueprintBuildingTableScreenHandler with empty context
     * @param syncId
     * The syncId for Client-Server Synchronization
     * @param playerInventory
     * The PlayerInventory of the PlayerEntity that opened the ScreenHandler
     */
    public BlueprintBuildingTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    /**
     * Server Constructor
     * @param syncId
     * The syncId for Client-Server Synchronization
     * @param playerInventory
     * The PlayerInventory of the PlayerEntity that opened the ScreenHandler
     * @param context
     * The ScreenHandlerContext
     */
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
        if (slotIndex == SlotIndex.BLUEPRINT.index()) {
            this.blueprint = stack;
            this.markDirty();
        } else if (slotIndex == SlotIndex.BASE.index()) {
            this.base = stack;
            this.markDirty();
        } else if  (slotIndex == SlotIndex.RESULT.index()) {
            this.result = stack;
        }
    }

    @Override
    public void markDirty() {
        this.result = ItemStack.EMPTY;
        PocketContraptions.LOGGER.info("markDirty called by {}", (this.context.equals(ScreenHandlerContext.EMPTY) ? "Client" : "Server"));
        context.run((world, blockPos) -> {
            PocketContraptions.LOGGER.info("Entered context call");
            if (!world.isClient()) {
                ItemStack result = this.updateRecipe((ServerWorld) world, this.plan);
                if (!result.isEmpty()) {
                    PocketContraptions.LOGGER.info("Crafting {}", result);
                    this.getSlot(SlotIndex.RESULT.index()).setStack(result);
//                    this.result = result;
                    PocketContraptions.LOGGER.info("Craft successful, result is now {}", this.getSlot(SlotIndex.RESULT.index()).getStack());
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

    @Override
    protected boolean canTakeResult() {
        boolean canTakeResult = this.plan.verify();
        if (!canTakeResult) {
            this.markDirty();
        }
        return canTakeResult;
    }

    @Override
    protected void onResultTaken() {
        if (this.plan.extract())
            this.base.decrement(1);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        this.result = ItemStack.EMPTY;
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this));
    }
}
