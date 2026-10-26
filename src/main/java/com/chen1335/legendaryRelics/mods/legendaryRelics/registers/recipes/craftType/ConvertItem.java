package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.recipes.craftType;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.recipes.EquipmentWorkbenchCraftInput;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ConvertItem(ItemStack itemStack) implements ICraftType {
    public static final MapCodec<ConvertItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(ConvertItem::itemStack)
    ).apply(instance, ConvertItem::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConvertItem> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            ConvertItem::itemStack,
            ConvertItem::new
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
        ItemStack result = itemStack.copyWithCount(itemStack.getCount());
        ((PatchedDataComponentMap) result.getComponents()).applyPatch(input.getMainItem().getComponentsPatch());
        return result;
    }
}
