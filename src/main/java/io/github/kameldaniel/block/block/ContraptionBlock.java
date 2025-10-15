package io.github.kameldaniel.block.block;

import com.mojang.serialization.MapCodec;
import io.github.kameldaniel.block.entity.ContraptionBlockEntity;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.item.QuantumCore;
import io.github.kameldaniel.world.dimension.PocketDimension;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ContraptionBlock extends BlockWithEntity {
    public ContraptionBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(ContraptionBlock::new);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ContraptionBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Ensure this method only runs once per use
        //   Picks the Main hand
        if (!world.isClient() && hand == Hand.MAIN_HAND &&
                world.getBlockEntity(pos) instanceof ContraptionBlockEntity contraptionBlockEntity) {
            if (stack.isEmpty()) {
                // Since this method only runs for the main hand, switch to off-hand if main is empty
                //   If both empty, hand is irrelevant
                hand = Hand.OFF_HAND;
                stack = player.getStackInHand(hand);
            }
            if (player.isSneaking() && stack.isEmpty() && contraptionBlockEntity.isBound()) {
                // If the player uses while sneaking with an empty hand, bound core should be dropped
                player.giveOrDropStack(contraptionBlockEntity.unbind());
                return ActionResult.SUCCESS;
                // If the player is sneaking, but hand is not empty, pass
            } else if (player.isSneaking()) return ActionResult.PASS;
            // Player is not sneaking
            if (contraptionBlockEntity.isBound()) {
                // The ContraptionBlock is bound to a dimension and the player is not sneaking; should teleport
                int dimID = Objects.requireNonNull(
                                Objects.requireNonNull(
                                        contraptionBlockEntity.getBoundCore()).get(ModComponents.DIM_KEY));
                PocketDimension.tpEntity(player, dimID);
                return ActionResult.SUCCESS;
            } else if (stack.getItem() instanceof QuantumCore core) {
                // If the player is holding an unbound QuantumCore, it should be bound before binding to the ContraptionBlock
                if (!stack.getComponents().contains(ModComponents.DIM_KEY)) core.use(world, player, hand);
                // QuantumCore has now certainly been bound, bind to ContraptionBlock
                contraptionBlockEntity.bindCore(stack.copyAndEmpty());
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
        // STILL ALLOWS QUANTUM CORE USE TO RUN!!!!! MUST FIX
        // Actually this mechanic is very likely to change
    }

    // Make sure to drop the bound core, and if not in creative, the block itself
    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!player.isCreative()) Block.dropStack(world, pos, new ItemStack(this.asItem()));
        if (!world.isClient() && world.getBlockEntity(pos) instanceof ContraptionBlockEntity contraptionBlockEntity
                && contraptionBlockEntity.isBound()) {
            Block.dropStack(world, pos, contraptionBlockEntity.unbind());
        }
        return super.onBreak(world, pos, state, player);
    }
}