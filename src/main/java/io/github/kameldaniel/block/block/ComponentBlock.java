package io.github.kameldaniel.block.block;

import com.mojang.serialization.MapCodec;
import io.github.kameldaniel.block.entity.ComponentBlockEntity;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ComponentBlock extends BlockWithEntity {
    public ComponentBlock(AbstractBlock.Settings settings) {
        super(settings.strength(-1.0f, Float.MAX_VALUE));
        PlayerBlockBreakEvents.BEFORE.register(
                (world, player, pos, state, blockEntity)
                        -> !(state.getBlock() instanceof ComponentBlock));
    }

    // Removes breaking particles
    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        return state;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(ComponentBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ComponentBlockEntity(pos, state);
    }
}