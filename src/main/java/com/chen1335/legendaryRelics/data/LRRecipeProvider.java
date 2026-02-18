package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.PURGATORY_TALISMAN)
                .define('A', LRItems.ANCIENT_FRAGMENT)
                .define('B', LRItems.LAVA_RING)
                .define('C', LRItems.NETHER_TALISMAN)
                .define('D', LRItems.NETHER_RING)
                .define('E', Items.GOLD_INGOT)
                .pattern(" A ")
                .pattern("BCD")
                .pattern(" E ")
                .unlockedBy("has_nether_talisman", has(LRItems.NETHER_TALISMAN))
                .showNotification(false)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.PERSEVERANCE_NECKLACE)
                .define('A', LRItems.ANCIENT_FRAGMENT)
                .define('B', Items.DIAMOND)
                .define('C', LRItems.HEALING_TALISMAN)
                .define('D', Items.STRING)
                .pattern("DAD")
                .pattern("BCB")
                .pattern(" B ")
                .unlockedBy("has_healing_talisman", has(LRItems.HEALING_TALISMAN))
                .showNotification(false)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.BLACK_DRAGON_HELMET)
                .define('A', LRItems.DRAGON_SCALE)
                .define('B', Items.NETHERITE_HELMET)
                .define('C', LRItems.ANCIENT_FRAGMENT)
                .define('D', LRItems.DARK_GOLD_FRAGMENT)
                .pattern("CAC")
                .pattern("ABA")
                .pattern(" D ")
                .unlockedBy("has_dark_gold_fragment", has(LRItems.DARK_GOLD_FRAGMENT))
                .showNotification(false)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.BLACK_DRAGON_CHEST_PLATE)
                .define('A', LRItems.DRAGON_SCALE)
                .define('B', Items.NETHERITE_CHESTPLATE)
                .define('C', LRItems.ANCIENT_FRAGMENT)
                .define('D', LRItems.DARK_GOLD_FRAGMENT)
                .pattern("CAC")
                .pattern("ABA")
                .pattern(" D ")
                .unlockedBy("has_dark_gold_fragment", has(LRItems.DARK_GOLD_FRAGMENT))
                .showNotification(false)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.BLACK_DRAGON_LEGGINGS)
                .define('A', LRItems.DRAGON_SCALE)
                .define('B', Items.NETHERITE_LEGGINGS)
                .define('C', LRItems.ANCIENT_FRAGMENT)
                .define('D', LRItems.DARK_GOLD_FRAGMENT)
                .pattern("CAC")
                .pattern("ABA")
                .pattern(" D ")
                .unlockedBy("has_dark_gold_fragment", has(LRItems.DARK_GOLD_FRAGMENT))
                .showNotification(false)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.BLACK_DRAGON_BOOTS)
                .define('A', LRItems.DRAGON_SCALE)
                .define('B', Items.NETHERITE_BOOTS)
                .define('C', LRItems.ANCIENT_FRAGMENT)
                .define('D', LRItems.DARK_GOLD_FRAGMENT)
                .pattern("CAC")
                .pattern("ABA")
                .pattern(" D ")
                .unlockedBy("has_dark_gold_fragment", has(LRItems.DARK_GOLD_FRAGMENT))
                .showNotification(false)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.DARK_GOLD)
                .define('A', Items.NETHERITE_INGOT)
                .define('B', LRItems.ANCIENT_FRAGMENT)
                .define('C', LRItems.DARK_GOLD_FRAGMENT)
                .pattern(" C ")
                .pattern("BAB")
                .pattern(" C ")
                .unlockedBy("has_dark_gold_fragment", has(LRItems.DARK_GOLD_FRAGMENT))
                .showNotification(false)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.DARK_GOLD_FORGING_TOOL)
                .define('A', LRItems.DARK_GOLD)
                .define('B', Items.OBSIDIAN)
                .define('C', Items.IRON_INGOT)
                .pattern(" C ")
                .pattern("BAB")
                .pattern(" C ")
                .unlockedBy("has_dark_gold", has(LRItems.DARK_GOLD))
                .showNotification(false)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, LRItems.WITHERING_BLADE)
                .define('A', Items.CRYING_OBSIDIAN)
                .define('B', Items.NETHERITE_INGOT)
                .define('C', LRItems.WITHER_SPIRIT)
                .define('D', LRItems.DARK_GOLD_FRAGMENT)
                .pattern(" DC")
                .pattern(" BD")
                .pattern("A  ")
                .unlockedBy("has_dark_gold", has(LRItems.DARK_GOLD))
                .showNotification(true)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, LRItems.REAPER)
                .define('A', Items.NETHERITE_INGOT)
                .define('B', LRItems.WITHER_SPIRIT)
                .define('C', Items.OBSIDIAN)
                .define('D', LRItems.DARK_GOLD_FRAGMENT)
                .pattern("AAB")
                .pattern("DCC")
                .pattern("CDC")
                .unlockedBy("has_dark_gold", has(LRItems.DARK_GOLD))
                .showNotification(true)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, LRItems.CHARM_OF_FRESH_START)
                .define('A', Items.STRING)
                .define('B', LRItems.ANCIENT_FRAGMENT)
                .pattern(" A ")
                .pattern("A A")
                .pattern(" B ")
                .unlockedBy("has_dark_gold", has(LRItems.DARK_GOLD))
                .showNotification(true)
                .save(recipeOutput);

        
    }
}
