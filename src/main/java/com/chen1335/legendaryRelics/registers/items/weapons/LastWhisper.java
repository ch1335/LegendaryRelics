package com.chen1335.legendaryRelics.registers.items.weapons;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.chen1335.legendaryRelics.API.IRenderArrowBow;
import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public class LastWhisper extends BowItem implements IEffectEquipment, ILRItemExtension, IRenderArrowBow {
    public LastWhisper() {
        super(new Properties().stacksTo(1).rarity(Rarity.EPIC).durability(2031));
    }


    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(LREquipmentEffectTypes.PERFORATION.value().create(1, EquipmentTypes.HANDS));
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

    @Override
    public void onEquipmentChangeFrom(ItemStack from, ItemStack to, LivingEntity entity) {
        if (!from.is(to.getItem())) {
            from.remove(LRDataComponentTypes.BOW_USING_ARROW);
        }
    }

    @Override
    public void onEquipmentChangeTo(ItemStack from, ItemStack to, LivingEntity entity) {
        if (!from.is(to.getItem())) {
            to.remove(LRDataComponentTypes.BOW_USING_ARROW);
        }
    }
}
