package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.common.AttributeModifierHolder;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.HardenedEffect;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class HardenedRing extends LRCuriosBase {

    public HardenedRing() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static Multimap<Holder<Attribute>, AttributeModifierHolder> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ARMOR, new AttributeModifierHolder(2, AttributeModifier.Operation.ADD_VALUE)
    );

    @Override
    public List<BaseEffect> EE$getDefaultEffects() {
        return List.of(new HardenedEffect(1));
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifierHolder> getAttributeModifierHolders() {
        return ATTRIBUTE_MODIFIERS;
    }
}
