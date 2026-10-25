package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.LRGeoArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

/**
 * 黑龙四件套共用一套 GeckoLib 模型，部位由骨骼名区分。
 */
public class BlackDragonArmor extends LRGeoArmor {

    public BlackDragonArmor(Type type, Properties properties) {
        super(Tier.Armor.BLACK_DRAGON, type, properties.stacksTo(1));
    }

    @Override
    protected String geoName() {
        return "black_dragon_armor";
    }

    @Override
    protected ItemAttributeModifiers buildAttributes() {
        ResourceLocation resourcelocation = LegendaryRelics.id("armor.black_dragon." + type.getName());
        EquipmentSlotGroup equipmentslotgroup = EquipmentSlotGroup.bySlot(type.getSlot());
        return this.getDefaultAttributeModifiers().withModifierAdded(
                Attributes.MAX_HEALTH, new AttributeModifier(resourcelocation, 5, AttributeModifier.Operation.ADD_VALUE), equipmentslotgroup
        );
    }


    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }
}
