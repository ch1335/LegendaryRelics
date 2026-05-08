package com.chen1335.legendaryRelics.registers.recipes;

import com.chen1335.legendaryRelics.API.objects.LRRecipe;
import com.chen1335.legendaryRelics.registers.recipes.craftType.CraftTypes;
import com.chen1335.legendaryRelics.registers.recipes.craftType.ICraftType;
import com.chen1335.legendaryRelics.registers.recipes.ingredients.IngredientWithSize;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.function.Function;

public record EquipmentWorkbenchCraft(
        Ingredient mainItem,
        NonNullList<IngredientWithSize> secondaryItems,
        ICraftType result) implements Recipe<EquipmentWorkbenchCraftInput> {

    @Override
    public boolean matches(EquipmentWorkbenchCraftInput input, Level level) {
        if (!mainItem.test(input.getMainItem())) {
            return false;
        } else if (input.size() != secondaryItems.size()) {
            return false;
        } else {
            ArrayList<ItemStack> nonEmptyItems = new ArrayList<>(input.size());
            for (ItemStack item : input.getSecondaryItems())
                if (!item.isEmpty())
                    nonEmptyItems.add(item);
            return RecipeMatcher.findMatches(nonEmptyItems, this.secondaryItems) != null;
        }
    }

    @Override
    public ItemStack assemble(EquipmentWorkbenchCraftInput input, HolderLookup.Provider registries) {
        return result.assemble(input, registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LRRecipe.EQUIPMENT_CRAFT_SERIALIZER.value();
    }

    @Override
    public RecipeType<?> getType() {
        return LRRecipe.EQUIPMENT_CRAFT.value();
    }

    public static class Serializer implements RecipeSerializer<EquipmentWorkbenchCraft> {
        private static final MapCodec<EquipmentWorkbenchCraft> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("mainItem").forGetter(EquipmentWorkbenchCraft::mainItem),
                IngredientWithSize.CODEC.listOf().xmap(NonNullList::copyOf, Function.identity()).fieldOf("secondaryItems").forGetter(EquipmentWorkbenchCraft::secondaryItems),
                CraftTypes.DISPATCH_CODEC.fieldOf("result").forGetter(EquipmentWorkbenchCraft::result)

        ).apply(instance, EquipmentWorkbenchCraft::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, EquipmentWorkbenchCraft> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                EquipmentWorkbenchCraft::mainItem,
                IngredientWithSize.STREAM_CODEC.apply(ByteBufCodecs.list()).map(NonNullList::copyOf, Function.identity()),
                EquipmentWorkbenchCraft::secondaryItems,
                CraftTypes.DISPATCH_STREAM_CODEC,
                EquipmentWorkbenchCraft::result,
                EquipmentWorkbenchCraft::new
        );

        @Override
        public MapCodec<EquipmentWorkbenchCraft> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EquipmentWorkbenchCraft> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
