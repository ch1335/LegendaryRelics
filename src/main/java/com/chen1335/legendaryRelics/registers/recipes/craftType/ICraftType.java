package com.chen1335.legendaryRelics.registers.recipes.craftType;

import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraftInput;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public interface ICraftType {


    MapCodec<? extends ICraftType> codec();

    StreamCodec<RegistryFriendlyByteBuf, ? extends ICraftType> streamCodec();


    ItemStack assemble(EquipmentWorkbenchCraftInput input, HolderLookup.Provider registries);
}
