package com.chen1335.legendaryRelics.registers.recipes.ingredients;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class IngredientWithSize implements Predicate<ItemStack> {

    private final Ingredient ingredient;
    private int count;

    public IngredientWithSize(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }


    public Ingredient ingredient() {
        return ingredient;
    }

    public int count() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static final Codec<IngredientWithSize> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientWithSize::ingredient),
                    Codec.INT.fieldOf("count").forGetter(IngredientWithSize::count)
            ).apply(instance, IngredientWithSize::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientWithSize> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            IngredientWithSize::ingredient,
            ByteBufCodecs.INT,
            IngredientWithSize::count,
            IngredientWithSize::new
    );

    public static IngredientWithSize of(ItemLike itemLike, int count) {
        return new IngredientWithSize(Ingredient.of(itemLike), count);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ingredient.test(itemStack) && itemStack.getCount() >= count;
    }


    public static IngredientWithSize of(ItemLike itemLike) {
        return new IngredientWithSize(Ingredient.of(itemLike), 1);
    }

    public static IngredientWithSize of(ItemStack itemStack) {
        return new IngredientWithSize(Ingredient.of(itemStack), itemStack.getCount());
    }

    public List<ItemStack> getItemStacks() {
        return Arrays.stream(ingredient.getItems()).map(itemStack -> itemStack.copyWithCount(count)).toList();
    }
}
