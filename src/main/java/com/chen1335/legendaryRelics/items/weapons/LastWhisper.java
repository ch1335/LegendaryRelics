package com.chen1335.legendaryRelics.items.weapons;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.equipmentEffects.weaponEffects.Perforation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public class LastWhisper extends BowItem implements IEffectEquipment {
    public LastWhisper() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(new Perforation(1));
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        return super.customArrow(arrow, projectileStack, weaponStack);
    }


    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers defaultAttributeModifiers = super.getDefaultAttributeModifiers(stack);
        return defaultAttributeModifiers
                .withModifierAdded(AttributesGetter.armorShred(), new AttributeModifier(LegendaryRelics.id("last_whisper"), 0.2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
                .withModifierAdded(AttributesGetter.arrowDamage(), new AttributeModifier(LegendaryRelics.id("last_whisper"), 0.3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HAND);

    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !(oldStack.getItem() == newStack.getItem() && oldStack.is(this));
    }
}
