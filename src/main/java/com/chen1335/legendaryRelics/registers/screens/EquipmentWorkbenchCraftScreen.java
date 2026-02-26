package com.chen1335.legendaryRelics.registers.screens;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.menus.EquipmentWorkbenchCraftMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EquipmentWorkbenchCraftScreen extends AbstractContainerScreen<EquipmentWorkbenchCraftMenu> {
    public static final ResourceLocation ICON = LegendaryRelics.id("textures/gui/container/equipment_workbench_craft.png");


    public EquipmentWorkbenchCraftScreen(EquipmentWorkbenchCraftMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 175;
        imageHeight = 217;
        inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(ICON, leftPos, topPos, 0, 0, 175, 217);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
