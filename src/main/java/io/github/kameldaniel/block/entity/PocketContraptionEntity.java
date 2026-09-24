package io.github.kameldaniel.block.entity;

import io.github.kameldaniel.block.ModBlockEntities;
import io.github.kameldaniel.component.ModComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class PocketContraptionEntity extends BlockEntity implements SidedInventory {
    private static final String DIM_KEY_DATA_KEY = "dim_key";

    private int dimKey = -1;

    public PocketContraptionEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POCKET_CONTRAPTION, pos, state);
    }

    @Override
    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        if  (this.dimKey != -1) {
            builder.add(ModComponents.DIM_KEY, this.dimKey);
        }
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        this.dimKey = components.getOrDefault(ModComponents.DIM_KEY, this.dimKey);
        super.markDirty();
    }

    @Override
    protected void readData(ReadView view) {
        this.dimKey = view.getInt(DIM_KEY_DATA_KEY, this.dimKey);
        super.markDirty();
    }

    @Override
    protected void writeData(WriteView view) {
        view.putInt(DIM_KEY_DATA_KEY, this.dimKey);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {

    }
}
