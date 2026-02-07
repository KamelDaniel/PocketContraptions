package io.github.kameldaniel.screenhandler;

import io.github.kameldaniel.block.ModBlocks;
import io.github.kameldaniel.block.entity.BlueprintBuilderEntity;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.payload.BlockPosPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class BlueprintBuilderScreenHandler extends ScreenHandler {
    private final BlueprintBuilderEntity ENTITY;
    private final ScreenHandlerContext CONTEXT;

    // Client Constructor
    public BlueprintBuilderScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (BlueprintBuilderEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    // Server Constructor
    public BlueprintBuilderScreenHandler(int syncId, PlayerInventory playerInventory, BlueprintBuilderEntity entity) {
        super(ModScreenHandlers.BLUEPRINT_BUILDER, syncId);

        this.ENTITY = entity;
        this.CONTEXT = ScreenHandlerContext.create(entity.getWorld(), entity.getPos());

        // Base Slot:
        Slot baseSlot = new Slot(this.ENTITY, 0, 44, 30) {
            @Override
            public boolean canInsert(ItemStack stack) {
                ItemStack blueprint = getSlot(1).getStack();
                return super.canInsert(stack) && !blueprint.isEmpty() &&
                        blueprint.get(ModComponents.BLUEPRINT).base().equals(Registries.ITEM.getId(stack.getItem()));
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        };

        // Blueprint Slot:
        Slot blueprintSlot = new Slot(this.ENTITY, 1, 80, 55) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return super.canInsert(stack) && stack.getComponents().contains(ModComponents.BLUEPRINT);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                super.onTakeItem(player, stack);
                if (!baseSlot.getStack().isEmpty())
                    quickMove(player, 0);
                if (!baseSlot.getStack().isEmpty()) {
                    ItemStack stackToDrop = baseSlot.getStack();
                    baseSlot.setStack(ItemStack.EMPTY);
                    player.dropItem(stackToDrop, false);
                }
            }
        };

        // Output Slot:
        Slot outputSlot = new Slot(this.ENTITY, 2, 119, 30) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        };

        addSlot(baseSlot);
        addSlot(blueprintSlot);
        addSlot(outputSlot);

        super.addPlayerHotbarSlots(playerInventory, 8, 142);
        super.addPlayerInventorySlots(playerInventory, 8, 84);
    }

    private void kickSlotsToPlayer(PlayerEntity player, int[] slots) {
        // Move items to player's inventory or drop if full
        Slot slot;
        ItemStack stackToDrop;
        for (int slotIndex : slots) {
            this.quickMove(player, slotIndex);

            slot = super.getSlot(slotIndex);
            stackToDrop = slot.getStack();
            slot.setStack(ItemStack.EMPTY);
            player.dropItem(stackToDrop, false);
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack stackToMove;
        if (slotIndex == 0) {
            // Move base to inventory
            stackToMove = this.ENTITY.removeStack(slotIndex);
            if (!super.insertItem(stackToMove, 3, super.slots.size(), false))  return ItemStack.EMPTY;
        } else if (slotIndex == 1) {
            // Move blueprint to inventory
            stackToMove = this.ENTITY.removeStack(slotIndex);
            if (!super.insertItem(stackToMove, 3, super.slots.size(), false))  return ItemStack.EMPTY;
            else this.kickSlotsToPlayer(player, new int[]{0, 2});
        } else if (slotIndex == 2) {
            // Move output to inventory
            stackToMove = this.ENTITY.removeStack(slotIndex);
            if (!super.insertItem(stackToMove, 3, super.slots.size(), false))  return ItemStack.EMPTY;
        } else if (2 <= slotIndex && slotIndex < super.slots.size()) {
            // Move to blueprintSlot builder
            stackToMove = super.slots.get(slotIndex).getStack();
            if (!super.insertItem(stackToMove, 0, 2, true)) return ItemStack.EMPTY;
        } else stackToMove = ItemStack.EMPTY;
        return stackToMove;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        if (slotIndex == 1) kickSlotsToPlayer(player, new int[]{0, 2});
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.CONTEXT, player, ModBlocks.BLUEPRINT_BUILDER);
    }
}