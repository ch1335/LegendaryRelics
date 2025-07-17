package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.LRCurio;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.common.AttributeModifierHolder;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.AttributeBoostInNether;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;


public class PurgatoryTalisman extends CombineCurio {
    public PurgatoryTalisman() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1));
    }

    public static final Supplier<Set<LRCurio>> SUB_CURIOS = Suppliers.memoize(() -> Set.of(
            LRItems.LAVA_RING.value(),
            LRItems.NETHER_RING.value()
    ));
    public static Multimap<Holder<Attribute>, AttributeModifierHolder> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ATTACK_SPEED, new AttributeModifierHolder(0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            Attributes.ATTACK_DAMAGE, new AttributeModifierHolder(0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            Attributes.BURNING_TIME, new AttributeModifierHolder(-0.8F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)

    );


    @Override
    public Set<LRCurio> getCombinedCurios() {
        return SUB_CURIOS.get();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifierHolder> getAttributeModifierHolders() {
        return ATTRIBUTE_MODIFIERS;
    }

    @Override
    public List<BaseEffect> getDefaultEffect() {
        return List.of(new AttributeBoostInNether(2));
    }
}
