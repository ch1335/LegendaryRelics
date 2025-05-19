package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class LRRecipeProvider extends RecipeProvider {
    public LRRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput, HolderLookup.@NotNull Provider holderLookup) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.SHIELD_REGENERATOR)
                .define('A', Items.OBSIDIAN)
                .define('B', Items.REDSTONE)
                .define('C', Items.IRON_INGOT)
                .define('E', Items.DIAMOND)
                .pattern("CAC")
                .pattern("BEB")
                .pattern("CAC")
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .showNotification(false)
                .save(recipeOutput);
    }
}
