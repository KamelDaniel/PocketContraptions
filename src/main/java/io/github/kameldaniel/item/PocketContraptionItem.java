package io.github.kameldaniel.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class PocketContraptionItem extends BlockItem {
    public PocketContraptionItem(Block block, Settings settings) {
        super(block, settings.equippable(EquipmentSlot.HEAD));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof PlayerEntity player && slot == EquipmentSlot.HEAD) {
            player.sendMessage(Text.literal("It's on your head!"), true);
        }
    }
}
