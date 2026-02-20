package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.AttributeModifierHolder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class PerseveranceNecklace extends LRCuriosBase {
    public PerseveranceNecklace() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    public static Multimap<Holder<Attribute>, AttributeModifierHolder> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ARMOR, new AttributeModifierHolder(0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            Attributes.MAX_HEALTH, new AttributeModifierHolder(0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );

    @Override
    public Multimap<Holder<Attribute>, AttributeModifierHolder> getAttributeModifierHolders() {
        return ATTRIBUTE_MODIFIERS;
    }


    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(
                LREquipmentEffectTypes.HEAL_INCREASE_EFFECT.value().create(2, EquipmentType.CURIO),
                LREquipmentEffectTypes.HEAL_PER_SECOND_EFFECT.value().create(2, EquipmentType.CURIO),
                LREquipmentEffectTypes.PERSEVERANCE_EFFECT.value().create(2, EquipmentType.CURIO)
        );
    }
}
