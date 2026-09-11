package io.github.kameldaniel.screen;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.recipe.type.BlueprintBuilding;
import io.github.kameldaniel.screenhandler.BlueprintBuildingTableScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class BlueprintBuildingTableScreen extends AbstractBlueprintBuildingScreen<BlueprintBuildingTableScreenHandler> {
    public BlueprintBuildingTableScreen(BlueprintBuildingTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        super.backgroundWidth = 255;
        super.backgroundHeight = 165;
    }

    @Override
    protected void init() {
        super.init();
        addDrawableChild(new BlueprintBuildingChecklistWidget(super.x + 175, super.y + 19, 73, 140, this, super.textRenderer));
    }

    @Override
    BlueprintBuilding.Checklist getChecklist() {
        return super.handler.getBlueprint().getOrDefault(ModComponents.BLUEPRINT_CHECKLIST, null);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, PocketContraptions.id("textures/gui/blueprint_builder.png"),
                super.x, super.y, 0, 0, super.backgroundWidth, super.backgroundHeight, 512, 512);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        for (Element child : super.children())
            if (child.isMouseOver(mouseX, mouseY))
                child.mouseScrolled(mouseX, mouseY, horizontal, vertical);
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }
}
