package com.chen1335.legendaryRelics.items.armor.twistedSet;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;

public class TwistedBoots extends TwistedArmor {
    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/armor/twisted_boots.png");

    public TwistedBoots() {
        super(Type.BOOTS, new Properties());
    }

    @Override
    protected ItemAttributeModifiers buildAttributes() {
        ResourceLocation resourcelocation = LegendaryRelics.id("armor.twisted." + type.getName());
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        return super.buildAttributes().withModifierAdded(
                Attributes.MOVEMENT_SPEED, new AttributeModifier(resourcelocation, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentslotgroup
        );
    }


    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return TEXTURE;
    }
}
