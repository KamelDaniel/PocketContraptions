package io.github.kameldaniel.block.entity;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.ModBlockEntities;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.payload.BlockPosPayload;
import io.github.kameldaniel.screenhandler.BlueprintBuilderScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.RedstoneView.DIRECTIONS;


public class BlueprintBuilderEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPosPayload>, SidedInventory {
    private ItemStack base = ItemStack.EMPTY;
    private ItemStack blueprint = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;

    public BlueprintBuilderEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLUEPRINT_BUILDER, pos, state);
    }

    public void attemptCraft() {
        if (this.blueprint.isEmpty()) return;

        try (Transaction transaction = Transaction.openOuter()) {
            Map<Identifier, Integer> allIngredients = Map.copyOf(this.blueprint.get(ModComponents.BLUEPRINT).ingredients());
            Map<Identifier, Integer> requiredItems = new HashMap<>();
            Map<Identifier, Integer> availableItems = new HashMap<>();

            allIngredients.forEach((id, reqInit) -> {
                int required = reqInit;
                int available = 0;
                for (Direction dir : DIRECTIONS) {
                    Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos.offset(dir), dir.getOpposite());
                    if (storage == null) continue;
                    for (StorageView<ItemVariant> view : storage) {
                        if (view.isResourceBlank()) continue;
                        ItemVariant item = ItemVariant.of(Registries.ITEM.get(id));
                        int availableHere = (int) storage.extract(item, required, transaction);
                        available += availableHere;
                        required -= availableHere;
                        if (required == 0) break;
                    }
                    if (required == 0) break;
                }
                final int finalRequired = required;
                final int finalAvailable = available;
                requiredItems.compute(id, (key, val) -> finalRequired == 0 ? null : finalRequired);
                availableItems.compute(id, (key, val) -> finalAvailable == 0 ? null : finalAvailable);
            });

            ItemStack newBlueprint = this.removeStack(1);
            Identifier result = newBlueprint.get(ModComponents.BLUEPRINT).result();

            if (requiredItems.isEmpty() &&
                    newBlueprint.get(ModComponents.BLUEPRINT).base().equals(Registries.ITEM.getId(base.getItem())) &&
                    (this.output.isEmpty() ||
                            (Registries.ITEM.getId(this.output.getItem()).equals(result)) &&
                                    this.output.getCount() < this.output.getMaxCount())) {
                transaction.commit();
                this.removeStack(0);
                this.setStack(2, new ItemStack(Registries.ITEM.get(result), this.output.getCount() + 1));
                this.attemptCraft();
            } else {
                newBlueprint.set(ModComponents.REQUIRED_ITEMS, requiredItems);
                newBlueprint.set(ModComponents.AVAILABLE_ITEMS, availableItems);
            }
            this.setStack(1, newBlueprint);
        }
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        this.setStack(1, this.removeStack(1));
        super.onBlockReplaced(pos, oldState);
    }

    @Override
    protected void writeData(WriteView view) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(3);
        items.add(this.base);
        items.add(this.blueprint);
        items.add(this.output);
        Inventories.writeData(view, items);
    }

    @Override
    protected void readData(ReadView view) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(3, ItemStack.EMPTY);
        Inventories.readData(view, items);
        this.base = items.get(0);
        this.blueprint = items.get(1);
        this.output = items.get(2);
    }

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return new BlockPosPayload(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container." + PocketContraptions.MOD_ID + ".blueprint_builder");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BlueprintBuilderScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1, 2};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return (slot == 0 && this.base.isEmpty() && !this.blueprint.isEmpty()
                && this.blueprint.get(ModComponents.BLUEPRINT).base().equals(Registries.ITEM.getId(stack.getItem())))
                || (slot == 1 && this.blueprint.isEmpty() && stack.contains(ModComponents.BLUEPRINT));
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 2;
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return this.base.isEmpty() && this.blueprint.isEmpty() && this.output.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot == 0) return this.base;
        if (slot == 1) return this.blueprint;
        if (slot == 2) return this.output;
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack out = ItemStack.EMPTY;
        if (slot == 0) {
            out = this.base;
            this.base = ItemStack.EMPTY;
            super.markDirty();
        } else if (slot == 1) {
            out = this.blueprint;
            this.blueprint = ItemStack.EMPTY;
            out.remove(ModComponents.REQUIRED_ITEMS);
            out.remove(ModComponents.AVAILABLE_ITEMS);
            super.markDirty();
        } else if (slot == 2) {
            out = this.output;
            this.output = ItemStack.EMPTY;
            super.markDirty();
        }
        return out;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == 0) {
            this.base = stack;
            super.markDirty();
        } else if (slot == 1 && stack.contains(ModComponents.BLUEPRINT)) {
            if (!stack.contains(ModComponents.REQUIRED_ITEMS))
                stack.set(ModComponents.REQUIRED_ITEMS, new HashMap<>(stack.get(ModComponents.BLUEPRINT).ingredients()));
            if (!stack.contains(ModComponents.AVAILABLE_ITEMS))
                stack.set(ModComponents.AVAILABLE_ITEMS, new HashMap<>());
            this.blueprint = stack;
            super.markDirty();
        } else if (slot == 2) {
            this.output = stack;
            super.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.base = ItemStack.EMPTY;
        this.blueprint = ItemStack.EMPTY;
        this.output = ItemStack.EMPTY;
        super.markDirty();
    }
}