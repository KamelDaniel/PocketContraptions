package io.github.kameldaniel.screen;

import io.github.kameldaniel.PocketContraptions;
import io.github.kameldaniel.component.ModComponents;
import io.github.kameldaniel.screenhandler.BlueprintBuilderScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public class BlueprintBuilderScreen extends HandledScreen<BlueprintBuilderScreenHandler> {
    public BlueprintBuilderScreen(BlueprintBuilderScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        super.backgroundWidth = 256;
    }

    @Override
    protected void init() {
        super.init();
        addDrawableChild(new BlueprintBuilderIngredientsWidget(super.x, super.y, super.handler, super.textRenderer));
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, PocketContraptions.id("textures/gui/blueprint_builder.png"),
                super.x, super.y, 0, 0, super.backgroundWidth, super.backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        ItemStack blueprint = super.getScreenHandler().getSlot(1).getStack();
        if (blueprint.isEmpty()) return;
        context.drawItem(new ItemStack(Registries.ITEM.get(blueprint.get(ModComponents.BLUEPRINT).base())),
                super.x + 44, super.y + 52);
        context.drawItem(new ItemStack(Registries.ITEM.get(blueprint.get(ModComponents.BLUEPRINT).result())),
                super.x + 119, super.y + 52);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        for (Element child : super.children())
            if (child.isMouseOver(mouseX, mouseY))
                child.mouseScrolled(mouseX, mouseY, horizontal, vertical);
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }
}