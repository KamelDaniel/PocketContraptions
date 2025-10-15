package io.github.kameldaniel.block.entity;

import io.github.kameldaniel.block.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ContraptionBlockEntity extends BlockEntity {
    private ItemStack boundCore;

    public ContraptionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONTRAPTION_BLOCK, pos, state);
    }

    @Override
    protected void writeData(WriteView writeView) {
        if (this.boundCore != null) writeView.put("boundCore", ItemStack.CODEC, this.boundCore);
        super.writeData(writeView);
    }

    @Override
    protected void readData(ReadView readView) {
        super.readData(readView);
        this.boundCore = readView.read("boundCore", ItemStack.CODEC).orElse(null);
    }

    /**
     *
     * @return
     * true if the Contraption Block is bound to a dimension, false otherwise
     */
    public boolean isBound() {
        return this.boundCore != null;
    }

    /**
     *
     * @return
     *  a copy of the ItemStack the ContraptionBlock is bound to
     */
    @Nullable("Bound Core may be null")
    public ItemStack getBoundCore() {
        if (!this.isBound()) return null;
        return this.boundCore.copy();
    }

    /**
     * unbinds the ContraptionBlock from the ItemStack
     * @return
     * the ItemStack the Contraption block is bound to
     */
    public ItemStack unbind() {
        super.markDirty();
        ItemStack temp = this.boundCore;
        this.boundCore = null;
        return temp;
    }

    /**
     * binds the ContraptionBlock to an ItemStack
     * @param core
     * the ItemStack the ContraptionBlock should be cound to
     */
    public void bindCore(ItemStack core) {
        super.markDirty();
        this.boundCore = core;
    }
}