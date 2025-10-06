package io.github.kameldaniel;

import io.github.kameldaniel.item.ModItems;
import io.github.kameldaniel.component.ModComponents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;

import java.util.Objects;

public class PocketContraptionsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
            if (stack.isOf(ModItems.QUANTUM_CORE)) {
                try {
                    int dimID = Objects.requireNonNull(stack.get(ModComponents.DIM_KEY));
                    list.add(Text.translatable("item.pocket-contraptions.dim_id", dimID));
                } catch (NullPointerException e) {
                    list.add(Text.translatable("item.pocket-contraptions.dim_id", "Unbound"));
                }
            }
        });
	}
}