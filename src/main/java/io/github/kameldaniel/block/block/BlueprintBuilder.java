package io.github.kameldaniel.block.block;

import com.mojang.serialization.MapCodec;
import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.entity.BlueprintBuilderEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.*;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class BlueprintBuilder extends FacingBlock implements BlockEntityProvider {
    private boolean powered;

    private static final Text TITLE = Text.translatable(PocketContraptions.MOD_ID + ".container.blueprint_builder");

    public BlueprintBuilder(Settings settings) {
        super(settings);
        super.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected MapCodec<? extends FacingBlock> getCodec() {
        return createCodec(BlueprintBuilder::new);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && world.getBlockEntity(pos) instanceof BlueprintBuilderEntity entity) {
            player.openHandledScreen(entity);
        }
        return world.isClient ? ActionResult.SUCCESS : ActionResult.CONSUME;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world == null || world.isClient()) return;
        PocketContraptions.LOGGER.info("Update Detected!");
        boolean newPowered = world.isReceivingRedstonePower(pos);
        if (newPowered != powered) {
            powered = newPowered;
            if (powered) {
                BlueprintBuilderEntity entity = (BlueprintBuilderEntity) world.getBlockEntity(pos);
                if (entity != null) entity.attemptCraft();
            }
        }
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                new CraftingScreenHandler(i, playerInventory, ScreenHandlerContext.create(world, pos)), TITLE);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BlueprintBuilderEntity(pos, state);
    }
}
