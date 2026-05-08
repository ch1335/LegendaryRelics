package com.chen1335.legendaryRelics.registers.recipes.craftType;

import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraftInput;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record CraftItem(ItemStack itemStack) implements ICraftType {
    public static final MapCodec<CraftItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(CraftItem::itemStack)
    ).apply(instance, CraftItem::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CraftItem> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            CraftItem::itemStack,
            CraftItem::new
    );

    @Override
    public MapCodec<? extends ICraftType> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends ICraftType> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public ItemStack assemble(EquipmentWorkbenchCraftInput input, HolderLookup.Provider registries) {
        return itemStack.copy();
    }

    public static CraftItem of(ItemStack itemStack) {
        return new CraftItem(itemStack);
    }
}
