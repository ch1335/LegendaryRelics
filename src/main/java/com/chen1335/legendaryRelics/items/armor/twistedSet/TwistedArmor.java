package com.chen1335.legendaryRelics.items.armor.twistedSet;

import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.items.armor.LRArmorBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class TwistedArmor extends LRArmorBase {
    public TwistedArmor(Type type, Properties properties) {
        super(LRArmorMaterials.TWISTED, type, properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    protected ItemAttributeModifiers buildAttributes() {
        ResourceLocation resourcelocation = LegendaryRelics.id("armor.inferno." + type.getName());
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        return this.getDefaultAttributeModifiers().withModifierAdded(
                Attributes.MAX_HEALTH, new AttributeModifier(resourcelocation, 2.5, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
        ).withModifierAdded(
                Attributes.ATTACK_DAMAGE, new AttributeModifier(resourcelocation, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentslotgroup

        )

                ;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }
}
