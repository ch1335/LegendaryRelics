package com.chen1335.legendaryRelics.events;

import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerCraftEvent extends PlayerEvent {
    private final RecipeHolder<EquipmentWorkbenchCraft> currentRecipe;
    private ItemStack result;

    public PlayerCraftEvent(Player player, RecipeHolder<EquipmentWorkbenchCraft> currentRecipe, ItemStack result) {
        super(player);
        this.currentRecipe = currentRecipe;
        this.result = result;
    }

    public RecipeHolder<EquipmentWorkbenchCraft> getRecipe() {
        return currentRecipe;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }
}
