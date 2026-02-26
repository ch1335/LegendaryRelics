package com.chen1335.legendaryRelics.events;

import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraft;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.Event;

public class CraftResultJeiTooltipEvent extends Event {
    private final RecipeHolder<EquipmentWorkbenchCraft> holder;

    private final IRecipeSlotView iRecipeSlotView;
    private final ITooltipBuilder iTooltipBuilder;

    public CraftResultJeiTooltipEvent(RecipeHolder<EquipmentWorkbenchCraft> holder, IRecipeSlotView iRecipeSlotView, ITooltipBuilder iTooltipBuilder) {
        this.holder = holder;
        this.iRecipeSlotView = iRecipeSlotView;
        this.iTooltipBuilder = iTooltipBuilder;
    }

    public IRecipeSlotView getiRecipeSlotView() {
        return iRecipeSlotView;
    }

    public ITooltipBuilder getiTooltipBuilder() {
        return iTooltipBuilder;
    }

    public RecipeHolder<EquipmentWorkbenchCraft> getHolder() {
        return holder;
    }
}
