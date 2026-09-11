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
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
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
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.RedstoneView.DIRECTIONS;


public class BlueprintBuilderEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPosPayload>, SidedInventory {
    private ItemStack base = ItemStack.EMPTY;
    private final int BASE_SLOT = 0;
    private ItemStack blueprint = ItemStack.EMPTY;
    private final int BLUEPRINT_SLOT = 1;
    private final Direction FACING;

    public BlueprintBuilderEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLUEPRINT_BUILDER, pos, state);
        this.FACING = state.get(FacingBlock.FACING);
    }

    public void attemptCraft() {
        if (this.blueprint.isEmpty()) return;
//        try (Transaction transaction = Transaction.openOuter()) {
//            Map<Identifier, Integer> allIngredients = Map.copyOf(this.blueprint.get(ModComponents.BLUEPRINT).ingredients());
//            Map<Identifier, Integer> requiredItems = new HashMap<>();
//            Map<Identifier, Integer> availableItems = new HashMap<>();
//
//            allIngredients.forEach((id, reqInit) -> {
//                int required = reqInit;
//                int available = 0;
//                for (Direction dir : DIRECTIONS) {
//                    if (dir.equals(this.FACING)) continue;
//                    Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, pos.offset(dir), dir.getOpposite());
//                    if (storage == null) continue;
//                    for (StorageView<ItemVariant> view : storage) {
//                        if (view.isResourceBlank()) continue;
//                        ItemVariant item = ItemVariant.of(Registries.ITEM.get(id));
//                        int availableHere = (int) storage.extract(item, required, transaction);
//                        available += availableHere;
//                        required -= availableHere;
//                        if (required == 0) break;
//                    }
//                    if (required == 0) break;
//                }
//                final int finalRequired = required;
//                final int finalAvailable = available;
//                requiredItems.compute(id, (key, val) -> finalRequired == 0 ? null : finalRequired);
//                availableItems.compute(id, (key, val) -> finalAvailable == 0 ? null : finalAvailable);
//            });
//
//            ItemStack newBlueprint = this.removeStack(1);
//            Identifier result = newBlueprint.get(ModComponents.BLUEPRINT).result();
//
//            if (requiredItems.isEmpty() &&
//                    newBlueprint.get(ModComponents.BLUEPRINT).base().equals(Registries.ITEM.getId(base.getItem()))) {
//                this.removeStack(BLUEPRINT_SLOT);
//                if (world != null && !world.isClient()) {
//                    BlockPos outputBlockPos = pos.offset(FACING);
//                    Vec3d outputPos = Vec3d.ofCenter(outputBlockPos).offset(FACING, 0.5);
////                    Vec3d outputPos = Vec3d.ofCenter(outputBlockPos).add(Vec3d.of(FACING.getVector()).multiply(0.5));
//                    Storage<ItemVariant> outputInventory = ItemStorage.SIDED.find(world, outputBlockPos, FACING.getOpposite());
//                    ItemVariant output = ItemVariant.of(Registries.ITEM.get(result));
//
//                    boolean dropOutput = outputInventory == null;
//                    if (!dropOutput) dropOutput = outputInventory.insert(output, 1, transaction) > 0;
//                    transaction.commit();
//                    if (dropOutput) {
//                        // ITEM DISPENSER BEHAVIOR
//                        // *****************************************************************************
//                        // Move many methods to Block class. Entity should be minimal to save resources
//                        // *****************************************************************************
//                        ItemEntity item = new ItemEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), output.toStack());
//                        double speed = 0.2;
//                        item.setVelocity(Vec3d.ofCenter(FACING.getVector()).multiply(speed));
////                        item.setVelocity(speed * (pos.getX() - outputBlockPos.getX()),
////                                         speed * (pos.getX() - outputBlockPos.getX()),
////                                         speed * (pos.getX() - outputBlockPos.getX()));
//                        world.spawnEntity(item);
//                    }
//                }
//                this.attemptCraft();
//            } else {
//                newBlueprint.set(ModComponents.REQUIRED_ITEMS, requiredItems);
//                newBlueprint.set(ModComponents.AVAILABLE_ITEMS, availableItems);
//            }
//            this.setStack(BLUEPRINT_SLOT, newBlueprint);
//        }
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        this.setStack(BLUEPRINT_SLOT, this.removeStack(BLUEPRINT_SLOT));
        super.onBlockReplaced(pos, oldState);
    }

    @Override
    protected void writeData(WriteView view) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(3);
        items.add(this.base);
        items.add(this.blueprint);
        Inventories.writeData(view, items);
    }

    @Override
    protected void readData(ReadView view) {
        DefaultedList<ItemStack> items = DefaultedList.ofSize(3, ItemStack.EMPTY);
        Inventories.readData(view, items);
        this.base = items.get(BASE_SLOT);
        this.blueprint = items.get(BLUEPRINT_SLOT);
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
        return new int[]{BASE_SLOT, BLUEPRINT_SLOT};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
//        return (slot == BASE_SLOT && this.base.isEmpty() && !this.blueprint.isEmpty()
//                && this.blueprint.get(ModComponents.BLUEPRINT).base().equals(Registries.ITEM.getId(stack.getItem())))
//                || (slot == BLUEPRINT_SLOT && this.blueprint.isEmpty() && stack.contains(ModComponents.BLUEPRINT));
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.base.isEmpty() && this.blueprint.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot == BASE_SLOT) return this.base;
        if (slot == BLUEPRINT_SLOT) return this.blueprint;
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack out = ItemStack.EMPTY;
        if (slot == BASE_SLOT) {
            out = this.base;
            this.base = ItemStack.EMPTY;
            super.markDirty();
        } else if (slot == BLUEPRINT_SLOT) {
            out = this.blueprint;
            this.blueprint = ItemStack.EMPTY;
            out.remove(ModComponents.REQUIRED_ITEMS);
            out.remove(ModComponents.AVAILABLE_ITEMS);
            super.markDirty();
        }
        return out;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
//        if (slot == BASE_SLOT) {
//            this.base = stack;
//            super.markDirty();
//        } else if (slot == BLUEPRINT_SLOT && stack.contains(ModComponents.BLUEPRINT)) {
//            if (!stack.contains(ModComponents.REQUIRED_ITEMS))
//                stack.set(ModComponents.REQUIRED_ITEMS, new HashMap<>(stack.get(ModComponents.BLUEPRINT).ingredients()));
//            if (!stack.contains(ModComponents.AVAILABLE_ITEMS))
//                stack.set(ModComponents.AVAILABLE_ITEMS, new HashMap<>());
//            this.blueprint = stack;
//            super.markDirty();
//        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.base = ItemStack.EMPTY;
        this.blueprint = ItemStack.EMPTY;
        super.markDirty();
    }
}