package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.twistedSet;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.LRGeoArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 扭曲四件套共用一套 GeckoLib 模型，部位由骨骼名区分。
 */
public class TwistedArmor extends LRGeoArmor {

    public TwistedArmor(Type type, Properties properties) {
        super(Tier.Armor.TWISTED, type, properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    protected String geoName() {
        return "twisted_armor";
    }

    @Override
    protected ItemAttributeModifiers buildAttributes() {
        ResourceLocation resourcelocation = LegendaryRelics.id("armor.twisted." + type.getName());
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        return this.getDefaultAttributeModifiers().withModifierAdded(
                Attributes.MAX_HEALTH, new AttributeModifier(resourcelocation, 2, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
        ).withModifierAdded(
                AttributesGetter.arrowDamage(), new AttributeModifier(resourcelocation, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentslotgroup
        );
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}
