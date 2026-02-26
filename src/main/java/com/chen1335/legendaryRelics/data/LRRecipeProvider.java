package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.data.recipeBuilders.EquipmentWorkbenchCraftBuilder;
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

        EquipmentWorkbenchCraftBuilder.builder(LRItems.LAST_WHISPER, LRItems.WITHER_SPIRIT)
                .addSecondary(LRItems.DARK_GOLD_FRAGMENT, 2)
                .addSecondary(Items.OBSIDIAN, 2)
                .addSecondary(Items.STRING, 3)
                .save(recipeOutput);


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

        EquipmentWorkbenchCraftBuilder.builder(LRItems.PURGATORY_TALISMAN, LRItems.NETHER_TALISMAN)
                .addSecondary(LRItems.LAVA_RING, LRItems.NETHER_RING, LRItems.ANCIENT_FRAGMENT)
                .addSecondary(Items.GOLD_INGOT, 2)
                .save(recipeOutput);


        EquipmentWorkbenchCraftBuilder.builder(LRItems.PERSEVERANCE_NECKLACE, LRItems.HEALING_TALISMAN)
                .addSecondary(LRItems.ANCIENT_FRAGMENT)
                .addSecondary(Items.DIAMOND, 3)
                .save(recipeOutput);

        EquipmentWorkbenchCraftBuilder.builder(LRItems.BLACK_DRAGON_HELMET, Items.NETHERITE_HELMET)
                .addSecondary(LRItems.DARK_GOLD_FRAGMENT)
                .addSecondary(LRItems.ANCIENT_FRAGMENT, 2)
                .addSecondary(LRItems.DRAGON_SCALE, 3)
                .addSecondary(Items.OBSIDIAN, 4)
                .save(recipeOutput);

        EquipmentWorkbenchCraftBuilder.builder(LRItems.BLACK_DRAGON_CHEST_PLATE, Items.NETHERITE_CHESTPLATE)
                .addSecondary(LRItems.DARK_GOLD_FRAGMENT)
                .addSecondary(LRItems.ANCIENT_FRAGMENT, 2)
                .addSecondary(LRItems.DRAGON_SCALE, 3)
                .addSecondary(Items.OBSIDIAN, 4)
                .addSecondary(Items.PHANTOM_MEMBRANE, 4)
                .save(recipeOutput);

        EquipmentWorkbenchCraftBuilder.builder(LRItems.BLACK_DRAGON_LEGGINGS, Items.NETHERITE_LEGGINGS)
                .addSecondary(LRItems.DARK_GOLD_FRAGMENT)
                .addSecondary(LRItems.ANCIENT_FRAGMENT, 2)
                .addSecondary(LRItems.DRAGON_SCALE, 3)
                .addSecondary(Items.OBSIDIAN, 4)
                .save(recipeOutput);

        EquipmentWorkbenchCraftBuilder.builder(LRItems.BLACK_DRAGON_BOOTS, Items.NETHERITE_BOOTS)
                .addSecondary(LRItems.DARK_GOLD_FRAGMENT)
                .addSecondary(LRItems.ANCIENT_FRAGMENT, 2)
                .addSecondary(LRItems.DRAGON_SCALE, 3)
                .addSecondary(Items.OBSIDIAN, 4)
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


        EquipmentWorkbenchCraftBuilder.builder(LRItems.WITHERING_BLADE, LRItems.WITHER_SPIRIT)
                .addSecondary(Items.CRYING_OBSIDIAN, Items.NETHERITE_INGOT)
                .addSecondary(LRItems.DARK_GOLD_FRAGMENT, 2)
                .save(recipeOutput);

        EquipmentWorkbenchCraftBuilder.builder(LRItems.REAPER, LRItems.WITHER_SPIRIT)
                .addSecondary(Items.NETHERITE_INGOT, 2)
                .addSecondary(LRItems.DARK_GOLD_FRAGMENT, 2)
                .addSecondary(Items.OBSIDIAN, 3)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, LRItems.CHARM_OF_FRESH_START)
                .define('A', Items.STRING)
                .define('B', LRItems.ANCIENT_FRAGMENT)
                .pattern(" A ")
                .pattern("A A")
                .pattern(" B ")
                .unlockedBy("has_dark_gold", has(LRItems.DARK_GOLD))
                .showNotification(false)
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LRItems.EQUIPMENT_WORKBENCH)
                .define('A', LRItems.ANCIENT_FRAGMENT)
                .define('B', Items.SMITHING_TABLE)
                .define('C', Items.DIAMOND)
                .define('D', Items.GOLD_INGOT)
                .pattern(" A ")
                .pattern("CBC")
                .pattern("DDD")
                .unlockedBy("has_dark_gold", has(LRItems.DARK_GOLD))
                .showNotification(true)
                .save(recipeOutput);

        EquipmentWorkbenchCraftBuilder.builder(LRItems.INFERNO_HELMET, Items.NETHERITE_HELMET)
                .addSecondary(LRItems.WITHER_SPIRIT, LRItems.TYRANNICAL_ESSENCE)
                .addSecondary(Items.BLAZE_ROD, 2)
                .addSecondary(Items.GOLD_INGOT, 4)
                .addSecondary(Items.NETHER_WART_BLOCK, 4)
                .save(recipeOutput);
        EquipmentWorkbenchCraftBuilder.builder(LRItems.INFERNO_CHEST_PLATE, Items.NETHERITE_CHESTPLATE)
                .addSecondary(LRItems.WITHER_SPIRIT, LRItems.TYRANNICAL_ESSENCE)
                .addSecondary(Items.BLAZE_ROD, 2)
                .addSecondary(Items.GOLD_INGOT, 4)
                .addSecondary(Items.NETHER_WART_BLOCK, 4)
                .save(recipeOutput);
        EquipmentWorkbenchCraftBuilder.builder(LRItems.INFERNO_LEGGINGS, Items.NETHERITE_LEGGINGS)
                .addSecondary(LRItems.WITHER_SPIRIT, LRItems.TYRANNICAL_ESSENCE)
                .addSecondary(Items.BLAZE_ROD, 2)
                .addSecondary(Items.GOLD_INGOT, 4)
                .addSecondary(Items.NETHER_WART_BLOCK, 4)
                .save(recipeOutput);
        EquipmentWorkbenchCraftBuilder.builder(LRItems.INFERNO_BOOTS, Items.NETHERITE_BOOTS)
                .addSecondary(LRItems.WITHER_SPIRIT, LRItems.TYRANNICAL_ESSENCE)
                .addSecondary(Items.BLAZE_ROD, 2)
                .addSecondary(Items.GOLD_INGOT, 4)
                .addSecondary(Items.NETHER_WART_BLOCK, 4)
                .save(recipeOutput);


        EquipmentWorkbenchCraftBuilder.builder(LRItems.TWISTED_HELMET, Items.NETHERITE_HELMET)
                .addSecondary(LRItems.WITHER_SPIRIT, LRItems.TYRANNICAL_ESSENCE)
                .addSecondary(Items.ENDER_PEARL, 2)
                .addSecondary(Items.GOLD_INGOT, 4)
                .addSecondary(Items.WARPED_WART_BLOCK, 4)
                .save(recipeOutput);
        EquipmentWorkbenchCraftBuilder.builder(LRItems.TWISTED_CHEST_PLATE, Items.NETHERITE_CHESTPLATE)
                .addSecondary(LRItems.WITHER_SPIRIT, LRItems.TYRANNICAL_ESSENCE)
                .addSecondary(Items.ENDER_PEARL, 2)
                .addSecondary(Items.GOLD_INGOT, 4)
                .addSecondary(Items.WARPED_WART_BLOCK, 4)
                .save(recipeOutput);
        EquipmentWorkbenchCraftBuilder.builder(LRItems.TWISTED_LEGGINGS, Items.NETHERITE_LEGGINGS)
                .addSecondary(LRItems.WITHER_SPIRIT, LRItems.TYRANNICAL_ESSENCE)
                .addSecondary(Items.ENDER_PEARL, 2)
                .addSecondary(Items.GOLD_INGOT, 4)
                .addSecondary(Items.WARPED_WART_BLOCK, 4)
                .save(recipeOutput);
        EquipmentWorkbenchCraftBuilder.builder(LRItems.TWISTED_BOOTS, Items.NETHERITE_BOOTS)
                .addSecondary(LRItems.WITHER_SPIRIT, LRItems.TYRANNICAL_ESSENCE)
                .addSecondary(Items.ENDER_PEARL, 2)
                .addSecondary(Items.GOLD_INGOT, 4)
                .addSecondary(Items.WARPED_WART_BLOCK, 4)
                .save(recipeOutput);
    }
}
