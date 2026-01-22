package io.github.kameldaniel.block;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.block.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.function.Function;

public class ModBlocks {
    // Block Registration

    /**
     * Creates and registers a Block with the given tag under the mod identifier.
     * If shouldRegisterItem is true, an Item is also created and registered.
     * The Block is created by calling blockConstructor(settings)
     * @param name
     * the block tag without mod id; mod id is applied automatically
     * @param blockConstructor
     * the method to create the Block
     * @param settings
     * block settings
     * @param shouldRegisterItem
     * true if a BlockItem should be created and registered for the block
     * @return
     * the Block created using blockConstructor
     */
    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockConstructor, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        // Create a RegistryKey for the Block
        RegistryKey<Block> blockKey = PocketContraptions.getBlockKey(name);

        // Create the Block instance
        Block block = blockConstructor.apply(settings.registryKey(blockKey));

        if (shouldRegisterItem) {
            // Create a RegistryKey for the Item
            RegistryKey<Item> itemKey = PocketContraptions.getItemKey(name);

            // Create the Item instance
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey());

            // Register the Item
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        // Register the Block
        Registry.register(Registries.BLOCK, blockKey, block);

        return block;
    }

    public static void initialize() {}
}