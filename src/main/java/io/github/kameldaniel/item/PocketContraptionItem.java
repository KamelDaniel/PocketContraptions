package io.github.kameldaniel.item;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;

public class PocketContraptionItem extends BlockItem {
    public PocketContraptionItem(Block block, Settings settings) {
        super(block, settings.equippable(EquipmentSlot.HEAD).maxCount(1));
    }
}
