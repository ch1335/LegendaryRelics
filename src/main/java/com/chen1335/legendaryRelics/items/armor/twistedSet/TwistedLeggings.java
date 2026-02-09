package com.chen1335.legendaryRelics.items.armor.twistedSet;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TwistedLeggings extends TwistedArmor {
    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/armor/twisted_leggings.png");

    public TwistedLeggings() {
        super(Type.LEGGINGS, new Properties());
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return TEXTURE;
    }
}
