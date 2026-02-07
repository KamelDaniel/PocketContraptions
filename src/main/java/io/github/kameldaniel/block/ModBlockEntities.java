package io.github.kameldaniel.block;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.entity.BlueprintBuilderEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    // BlockEntity Registration
    public static final BlockEntityType<BlueprintBuilderEntity> BLUEPRINT_BUILDER =
            register("blueprint_builder_entity", BlueprintBuilderEntity::new, ModBlocks.BLUEPRINT_BUILDER);

    /**
     * Creates and registers a BlockEntityType with the given tag under the mod identifier
     * @param name
     * the block tag without mod id; mod id is applied automatically
     * @param blockEntityConstructor
     * the constructor that should be used to create a BlockEntity for a block in blocks
     * @param blocks
     * Blocks that should be attached to a BlockEntity of this type
     * @return
     * the BlockEntityType created
     */
    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> blockEntityConstructor,
            Block... blocks
    ) {
        // Create a tag
        Identifier id = PocketContraptions.id(name);
        // Create the BlockEntityType
        BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.<T>create(blockEntityConstructor, blocks).build();
        // Return and register the BlockEntityType
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, blockEntityType);
    }

    public static void initialize() {}
}
