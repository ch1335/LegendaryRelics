package com.chen1335.legendaryRelics.items.armor.infernoSet;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InfernoBoots extends InfernoArmor {
    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/armor/inferno_boots.png");


    public InfernoBoots() {
        super(Type.BOOTS, new Properties());
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.@NotNull Layer layer, boolean innerModel) {
        return TEXTURE;
    }
}
