package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class SoulBoots extends SoulArmor {
    public SoulBoots() {
        super(Type.BOOTS, new Properties());
    }

    @Override
    protected ItemAttributeModifiers buildAttributes() {
        ResourceLocation resourcelocation = LegendaryRelics.id("armor.soul." + type.getName());
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        return super.buildAttributes().withModifierAdded(
                Attributes.MOVEMENT_SPEED, new AttributeModifier(resourcelocation, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentslotgroup
        );
    }
}
