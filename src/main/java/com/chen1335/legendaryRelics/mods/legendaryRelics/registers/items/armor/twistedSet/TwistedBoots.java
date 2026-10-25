package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.twistedSet;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class TwistedBoots extends TwistedArmor {

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
}
