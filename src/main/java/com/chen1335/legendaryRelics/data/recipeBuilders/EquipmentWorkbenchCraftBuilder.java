package com.chen1335.legendaryRelics.data.recipeBuilders;

import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraft;
import com.chen1335.legendaryRelics.registers.recipes.craftType.CraftItem;
import com.chen1335.legendaryRelics.registers.recipes.craftType.ICraftType;
import com.chen1335.legendaryRelics.registers.recipes.ingredients.IngredientWithSize;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class EquipmentWorkbenchCraftBuilder implements RecipeBuilder {


    private ICraftType result;
    private final NonNullList<IngredientWithSize> secondaryItems = NonNullList.create();
    private Ingredient mainItem;

    public EquipmentWorkbenchCraftBuilder() {

    }

    public static EquipmentWorkbenchCraftBuilder builder(ItemStack result) {
        EquipmentWorkbenchCraftBuilder equipmentWorkbenchCraftBuilder = new EquipmentWorkbenchCraftBuilder();
        equipmentWorkbenchCraftBuilder.result = CraftItem.of(result);
        return equipmentWorkbenchCraftBuilder;
    }

    public static EquipmentWorkbenchCraftBuilder builder(ItemLike result, ItemLike mainItem) {
        EquipmentWorkbenchCraftBuilder builder = builder(result.asItem().getDefaultInstance());
        builder.mainItem = Ingredient.of(mainItem);
        return builder;
    }

    public static EquipmentWorkbenchCraftBuilder builder(ItemLike result, TagKey<Item> tagKey) {
        EquipmentWorkbenchCraftBuilder builder = builder(result.asItem().getDefaultInstance());
        builder.mainItem = Ingredient.of(tagKey);
        return builder;
    }


    public EquipmentWorkbenchCraftBuilder addSecondary(ItemLike itemLike) {
        secondaryItems.add(IngredientWithSize.of(itemLike));
        return this;
    }

    public EquipmentWorkbenchCraftBuilder addSecondary(ItemLike... itemLikes) {
        for (ItemLike itemLike : itemLikes) {
            addSecondary(itemLike);
        }
        return this;
    }

    public EquipmentWorkbenchCraftBuilder addSecondary(ItemLike itemLike, int count) {
        secondaryItems.add(IngredientWithSize.of(itemLike, count));
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        if (result instanceof CraftItem craftItem) {
            return craftItem.itemStack().getItem();
        }
        return Items.AIR;
    }


    @Override
    public void save(RecipeOutput recipeOutput) {
        if (result instanceof CraftItem) {
            RecipeBuilder.super.save(recipeOutput);
        }
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        if (secondaryItems.size() > 8) {
            throw new IllegalArgumentException("Secondary items cannot be more than 8");
        }
        ResourceLocation id1 = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "equipment_craft/" + id.getPath());
        recipeOutput.accept(id1, new EquipmentWorkbenchCraft(mainItem, secondaryItems, result), null);
    }
}
