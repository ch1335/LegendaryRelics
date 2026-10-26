package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.shadowAssassinSet;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.LRGeoArmor;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * 暗影刺客四件套共用一套 GeckoLib 模型，部位由骨骼名区分。
 */
public class ShadowAssassinArmor extends LRGeoArmor {

    public ShadowAssassinArmor(Type type, Properties properties) {
        super(Tier.Armor.SHADOW_ASSASSIN, type, properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    protected String geoName() {
        return "shadow_assassin_armor";
    }

    @Override
    protected ItemAttributeModifiers buildAttributes() {
        ResourceLocation resourcelocation = LegendaryRelics.id("armor.shadow_assassin." + type.getName());
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        return this.getDefaultAttributeModifiers().withModifierAdded(
                AttributesGetter.critChance(), new AttributeModifier(resourcelocation, 0.025, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
        ).withModifierAdded(
                AttributesGetter.critDamame(), new AttributeModifier(resourcelocation, 0.05, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup

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
