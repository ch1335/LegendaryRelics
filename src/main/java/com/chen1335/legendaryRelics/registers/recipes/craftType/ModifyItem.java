package com.chen1335.legendaryRelics.registers.recipes.craftType;

import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraftInput;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ModifyItem() implements ICraftType{
    @Override
    public MapCodec<? extends ICraftType> codec() {
        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends ICraftType> streamCodec() {
        return null;
    }

    @Override
    public ItemStack assemble(EquipmentWorkbenchCraftInput input, HolderLookup.Provider registries) {
        return null;
    }

}
