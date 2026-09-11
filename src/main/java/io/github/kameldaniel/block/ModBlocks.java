package io.github.kameldaniel.block;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.block.BlueprintBuilder;
import io.github.kameldaniel.block.block.BlueprintBuildingTable;
import io.github.kameldaniel.block.block.PocketContraption;
import io.github.kameldaniel.item.PocketContraptionItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ModBlocks {
    // Block Registration
    public static final Block BLUEPRINT_BUILDER = register("blueprint_builder",
            BlueprintBuilder::new, BlockItem::new);
    public static final Block BLUEPRINT_BUILDING_TABLE = register("blueprint_building_table",
            BlueprintBuildingTable::new, BlockItem::new);
    public static final Block POCKET_CONTRAPTION = register("pocket_contraption",
            PocketContraption::new, PocketContraptionItem::new);

    /**
     * Creates and registers a Block with the given tag under the mod identifier.
     * If shouldRegisterItem is true, an Item is also created and registered.
     * The Block is created by calling blockConstructor(settings)
     * @param name
     * the block tag without mod id; mod id is applied automatically
     * @param blockConstructor
     * the method to create the Block
     * @param itemConstructor
     * the constructor that consumes a Block and Item.Settings, and returns an Item to register
     * or null if no item should be registered
     * @return
     * the Block created using blockConstructor
     */
    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockConstructor, BiFunction<Block, Item.Settings, ? extends Item> itemConstructor) {
        // Create a RegistryKey for the Block
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, PocketContraptions.id(name));

        // Create the Block instance
        Block block = blockConstructor.apply(AbstractBlock.Settings.create().registryKey(blockKey));

        if (itemConstructor != null) {
            // Create a RegistryKey for the Item
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, PocketContraptions.id(name));

            // Create the Item instance
            Item item = itemConstructor.apply(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey());

            // Register the Item
            Registry.register(Registries.ITEM, itemKey, item);
        }

        // Register the Block
        Registry.register(Registries.BLOCK, blockKey, block);

        return block;
    }

    public static void initialize() {}
}