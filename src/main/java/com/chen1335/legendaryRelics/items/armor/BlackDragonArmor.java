package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlackDragonArmor extends ArmorItem {
    private static final ResourceLocation BlackDragonArmorTexture = LegendaryRelics.id("textures/armor/black_dragon.png");

    public BlackDragonArmor(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        return BlackDragonArmorTexture;
    }
}
