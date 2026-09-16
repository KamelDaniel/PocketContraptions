package io.github.kameldaniel.screenhandler;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;

@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
public abstract class AbstractBlueprintBuildingScreenHandler extends AbstractRecipeScreenHandler {

    /**
     * A screen handler for Blueprint Building recipes
     * @param screenHandlerType
     * The ScreenHandlerType of the child
     * @param syncId
     * The syncId for Client-Server synchronization
     */
    protected AbstractBlueprintBuildingScreenHandler(
            ScreenHandlerType<? extends AbstractBlueprintBuildingScreenHandler> screenHandlerType,
            int syncId) {
        super(screenHandlerType, syncId);
    }

    /**
     * Creates the blueprint Slot for the Screen Handler
     * - Slot views the blueprint used in the Blueprint Building recipe
     * @param inventory
     * The inventory containing the blueprint ItemStack
     * @param x
     * x coordinate to draw the Slot on the inventory texture
     * @param y
     * y coordinate to draw the Slot on the inventory texture
     * @return
     * The Slot that views the blueprint; Has max item count 1
     */
    protected Slot addBlueprintSlot(Inventory inventory, int x, int y) {
        return this.addSlot(new Slot(inventory, SlotIndex.BLUEPRINT.index(), x, y) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
    }

    /**
     * Creates the base Slot for the Screen Handler
     * - Slot views the base used in the Blueprint Building recipe
     * @param inventory
     * The inventory containing the base ItemStack
     * @param x
     * x coordinate to draw the Slot on the inventory texture
     * @param y
     * y coordinate to draw the Slot on the inventory texture
     * @return
     * The Slot that views the base; Has max item count 1
     */
    protected Slot addBaseSlot(Inventory inventory, int x, int y) {
        return this.addSlot(new Slot(inventory, SlotIndex.BASE.index(), x, y) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
    }

    /**
     * Creates the result Slot for the Screen Handler
     * - Slot views the output of the Blueprint Builder
     * @param inventory
     * The inventory containing the result ItemStack
     * @param x
     * x coordinate to draw the Slot on the inventory texture
     * @param y
     * y coordinate to draw the Slot on the inventory texture
     * @return
     * The Slot that views the result; Cannot insert
     */
    protected Slot addResultSlot(Inventory inventory, int x, int y) {
        return this.addSlot(new Slot(inventory, SlotIndex.RESULT.index(), x, y) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return canTakeResult();
            }

            @Override
            public ItemStack takeStack(int amount) {
                ItemStack stack = super.takeStack(amount);
                if (!stack.isEmpty()) {
                    onResultTaken();
                }
                return stack;
            }

            @Override
            public void markDirty() {
            }
        });
    }

    protected abstract boolean canTakeResult();

    protected abstract void onResultTaken();

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
