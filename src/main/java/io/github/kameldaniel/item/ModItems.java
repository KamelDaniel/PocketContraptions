package io.github.kameldaniel.item;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.function.Function;

public class ModItems {
    // Creative Mode Item Tab Registration
    public static final RegistryKey<ItemGroup> CUSTOM_ITEM_GROUP_KEY =
            RegistryKey.of(Registries.ITEM_GROUP.getKey(), PocketContraptions.id("item_group"));
    public static final ItemGroup CUSTOM_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Registries.ITEM.get(Identifier.ofVanilla("diamond_block"))))
            .displayName(Text.literal("Pocket Contraptions"))
            .build();

    // Item Registration
    public static final Item BLUEPRINT = register("blueprint", Item::new, new Item.Settings().maxCount(1));

    /**
     * Creates and registers an Item with the given tag under the mod identifier.
     * The Item is created by calling itemConstructor(settings)
     * @param name
     * the item tag without mod id; mod id is applied automatically
     * @param itemConstructor
     * the method to create the Item
     * @param settings
     * item settings
     * @return
     * the Item created using itemConstructor
     */
    public static Item register(String name, Function<Item.Settings, Item> itemConstructor, Item.Settings settings) {
        // Create a RegistryKey for the Item
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, PocketContraptions.id(name));

        // Create the Item instance
        Item item = itemConstructor.apply(settings.registryKey(itemKey));

        // Register the Item
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        // Register Item Group and add Items
        Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);
        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register(itemGroup -> {
            itemGroup.add(ModItems.BLUEPRINT);
            itemGroup.add(ModBlocks.BLUEPRINT_BUILDER.asItem());
        });
    }
}