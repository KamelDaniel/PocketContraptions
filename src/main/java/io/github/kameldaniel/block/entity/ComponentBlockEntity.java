package io.github.kameldaniel.block.entity;

import io.github.kameldaniel.block.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ComponentBlockEntity extends BlockEntity {
    public ComponentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPONENT_BLOCK, pos, state);
    }
    // WIP
}
