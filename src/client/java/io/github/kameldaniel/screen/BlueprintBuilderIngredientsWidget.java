package io.github.kameldaniel.screen;

import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.screenhandler.BlueprintBuilderScreenHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;


public class BlueprintBuilderIngredientsWidget extends ScrollableWidget {
    private final TextRenderer TEXT_RENDERER;

    private static final int LINE_HEIGHT = 16;

    private final Slot BLUEPRINT_SLOT;

    private Map<Identifier, Integer> getRequiredItems() {
        return this.BLUEPRINT_SLOT.getStack().isEmpty() ? new HashMap<>() :
                this.BLUEPRINT_SLOT.getStack().get(ModComponents.REQUIRED_ITEMS);
    }
    private Map<Identifier, Integer> getAvailableItems() {
        return this.BLUEPRINT_SLOT.getStack().isEmpty() ? new HashMap<>() :
                this.BLUEPRINT_SLOT.getStack().get(ModComponents.AVAILABLE_ITEMS);
    }

    public BlueprintBuilderIngredientsWidget(int x, int y, BlueprintBuilderScreenHandler handler, TextRenderer textRenderer) {
        super(x+177, y+10, 70, 147, Text.empty());
        this.TEXT_RENDERER = textRenderer;
        this.BLUEPRINT_SLOT = handler.getSlot(1);
    }

    @Override
    protected int getContentsHeightWithPadding() {
        int requiredItems = this.getRequiredItems().size();
        int availableItems = this.getAvailableItems().size();
        return (requiredItems + availableItems)*LINE_HEIGHT;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return LINE_HEIGHT;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Text tooltip = null;
        context.enableScissor(this.getX(), this.getY(), this.getX()+this.width, this.getY()+this.height);
        int x = getX();
        int startY = this.getY() - (int) this.getScrollY();
        int i = 0;
        for (Map.Entry<Identifier, Integer> pair : this.getRequiredItems().entrySet()) {
            Identifier key = pair.getKey();
            Integer count = pair.getValue();
            int y = startY + LINE_HEIGHT * i;
            context.drawItem(new ItemStack(Registries.ITEM.get(key)), x, y);
            context.drawText(this.TEXT_RENDERER, count.toString(), x + LINE_HEIGHT + 4,
                    y + 4, 0xFFFFFFFF, false);
            if (super.isMouseOver(mouseX, mouseY) && x <= mouseX && mouseX <= x + super.width && y <= mouseY && mouseY <= y + LINE_HEIGHT)
                tooltip = Text.translatable(count + "x " + Registries.ITEM.get(key).getName().getString());
            i++;
        }
        for (Map.Entry<Identifier, Integer> pair : this.getAvailableItems().entrySet()) {
            Identifier key = pair.getKey();
            Integer count = pair.getValue();
            int y = startY + LINE_HEIGHT * i;
            context.drawItem(new ItemStack(Registries.ITEM.get(key)), x, y);
            context.drawText(this.TEXT_RENDERER, count.toString(), x + LINE_HEIGHT + 4,
                    y + 4, 0xFF009900, false);
            context.drawHorizontalLine(x, x + super.width, y + 8, 0xFFFF0000);
            if (super.isMouseOver(mouseX, mouseY) && x <= mouseX && mouseX <= x + super.width && y <= mouseY && mouseY <= y + LINE_HEIGHT)
                tooltip = Text.translatable(count + "x " + Registries.ITEM.get(key).getName().getString());
            i++;
        }
        context.disableScissor();
        super.drawScrollbar(context);
        if (tooltip != null) context.drawTooltip(tooltip, mouseX, mouseY);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
