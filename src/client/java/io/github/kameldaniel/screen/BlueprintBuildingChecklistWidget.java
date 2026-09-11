package io.github.kameldaniel.screen;

import com.mojang.datafixers.util.Either;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import io.github.kameldaniel.screenhandler.AbstractBlueprintBuildingScreenHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.context.ContextParameterMap;

import java.util.List;


public class BlueprintBuildingChecklistWidget extends ScrollableWidget {
    private final AbstractBlueprintBuildingScreen<? extends AbstractBlueprintBuildingScreenHandler> screen;
    private final TextRenderer TEXT_RENDERER;

    private BlueprintBuilding.Checklist checklist = null;

    private static final int LINE_HEIGHT = 16;

    public BlueprintBuildingChecklistWidget(int x, int y, int width, int height, AbstractBlueprintBuildingScreen<? extends AbstractBlueprintBuildingScreenHandler> screen, TextRenderer textRenderer) {
        super(x, y, width, height, Text.empty());
        this.TEXT_RENDERER = textRenderer;
        this.screen = screen;
    }

    @Override
    protected int getContentsHeightWithPadding() {
        int requiredItems = checklist.required().size();
        int availableItems = checklist.available().size();
        return (requiredItems + availableItems) * LINE_HEIGHT;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return LINE_HEIGHT;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.checklist = this.screen.getChecklist();
        if (this.checklist == null) return;
        Text tooltip = null;
        context.enableScissor(this.getX(), this.getY(), this.getX()+this.width, this.getY()+this.height);
        int x = getX();
        int startY = this.getY() - (int) this.getScrollY();
        int i = 0;
        for (BlueprintBuilding.CountedIngredient ci : checklist.required()) {
            List<Item> validIngredients = ci.getIngredient().getMatchingItems().map(itemEntry ->
                    itemEntry.getKeyOrValue().right().orElse(Registries.ITEM.get(itemEntry.getKeyOrValue().left().orElse(null)))
            ).toList();
            int y = startY + LINE_HEIGHT * i;
            context.drawItem(new ItemStack(validIngredients.getFirst()), x, y);
            context.drawText(this.TEXT_RENDERER, String.valueOf(ci.getCount()), x + LINE_HEIGHT + 4,
                    y + 4, 0xFFFFFFFF, false);
            if (super.isMouseOver(mouseX, mouseY) && x <= mouseX && mouseX <= x + super.width && y <= mouseY && mouseY <= y + LINE_HEIGHT)
                tooltip = Text.translatable(ci.getCount() + "x " + ci.getIngredient());
            i++;
        }
        for (BlueprintBuilding.CountedIngredient ci : checklist.available()) {
            List<Item> validIngredients = ci.getIngredient().getMatchingItems().map(itemEntry ->
                    itemEntry.getKeyOrValue().right().orElse(Registries.ITEM.get(itemEntry.getKeyOrValue().left().orElse(null)))
            ).toList();
            int y = startY + LINE_HEIGHT * i;
            context.drawItem(new ItemStack(validIngredients.getFirst()), x, y);
            context.drawText(this.TEXT_RENDERER, String.valueOf(ci.getCount()), x + LINE_HEIGHT + 4,
                    y + 4, 0xFF009900, false);
            if (super.isMouseOver(mouseX, mouseY) && x <= mouseX && mouseX <= x + super.width && y <= mouseY && mouseY <= y + LINE_HEIGHT)
                tooltip = Text.translatable(ci.getCount() + "x " + ci.getIngredient());
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
