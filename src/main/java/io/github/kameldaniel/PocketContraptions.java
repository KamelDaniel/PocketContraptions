package io.github.kameldaniel;

import io.github.kameldaniel.block.ModBlocks;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.item.ModItems;
import io.github.kameldaniel.world.dimension.PocketDimension;
import net.fabricmc.api.ModInitializer;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PocketContraptions implements ModInitializer {
	public static final String MOD_ID = "pocket-contraptions";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static RegistryKey<Block> getBlockKey(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, name));
    }
    public static RegistryKey<Item> getItemKey(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
    }

	@Override
	public void onInitialize() {
        ModItems.initialize();
        ModBlocks.initialize();
        PocketDimension.initialize();
        ModComponents.initialize();
		LOGGER.info("Mod Initialized! Hello Fabric World!");
	}
}