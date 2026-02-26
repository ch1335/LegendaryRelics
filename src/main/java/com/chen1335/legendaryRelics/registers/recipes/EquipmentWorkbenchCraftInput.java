package com.chen1335.legendaryRelics.registers.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class EquipmentWorkbenchCraftInput implements RecipeInput {
    private final ItemStack mainItem;
    private final NonNullList<ItemStack> secondaryItems;

    public EquipmentWorkbenchCraftInput(ItemStack mainItem, List<ItemStack> secondary) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        this.mainItem = mainItem;
        for (ItemStack itemStack : secondary) {
            if (!itemStack.isEmpty()) {
                stacks.add(itemStack);
            }
        }
        secondaryItems = stacks;
    }

    @Override
    public ItemStack getItem(int index) {
        if (index == 0) {
            return mainItem;
        } else {
            return secondaryItems.get(index - 1);
        }
    }

    @Override
    public int size() {
        return secondaryItems.size();
    }

    public ItemStack getMainItem() {
        return mainItem;
    }

    public NonNullList<ItemStack> getSecondaryItems() {
        return secondaryItems;
    }
}
