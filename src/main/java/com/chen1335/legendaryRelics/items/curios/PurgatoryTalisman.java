package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.common.AttributeModifierHolder;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.AttributeBoostInNether;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.FireDamageReduce;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.InFireTargetDamageIncrease;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Rarity;

import java.util.List;


public class PurgatoryTalisman extends LRCuriosBase {
    public PurgatoryTalisman() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1));
    }


    public static Multimap<Holder<Attribute>, AttributeModifierHolder> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ATTACK_SPEED, new AttributeModifierHolder(0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            Attributes.ATTACK_DAMAGE, new AttributeModifierHolder(0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            Attributes.BURNING_TIME, new AttributeModifierHolder(-0.8F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );


    @Override
    public Multimap<Holder<Attribute>, AttributeModifierHolder> getAttributeModifierHolders() {
        return ATTRIBUTE_MODIFIERS;
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(new AttributeBoostInNether(2), new FireDamageReduce(1), new InFireTargetDamageIncrease(1));
    }
}
