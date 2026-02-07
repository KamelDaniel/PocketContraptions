package io.github.kameldaniel.block.block;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.entity.BlueprintBuilderEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class BlueprintBuilder extends Block implements BlockEntityProvider {
    private boolean powered;

    private static final Text TITLE = Text.translatable(PocketContraptions.MOD_ID + ".container.blueprint_builder");

    public BlueprintBuilder(Settings settings) {
        super(settings);
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
            if (powered) ((BlueprintBuilderEntity) world.getBlockEntity(pos)).attemptCraft();
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
