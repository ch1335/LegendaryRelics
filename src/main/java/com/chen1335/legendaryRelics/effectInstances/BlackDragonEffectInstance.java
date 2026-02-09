package com.chen1335.legendaryRelics.effectInstances;

import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.armorSetEffect.BlackDragonArmorSetEffect;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class BlackDragonEffectInstance extends EffectInstance {
    public static final ResourceLocation BLACK_DRAGON_ATTRIBUTE_MULTIPLIER = LegendaryRelics.id("black_dragon_attribute_multiplier");

    public BlackDragonEffectInstance(int piece) {
        super(piece);
    }

    @Override
    public void tick(LivingEntity living) {
        if (!living.level().isClientSide && living.level().getGameTime() % 10 == 0 && living.getHealth() < living.getMaxHealth()) {
            CalculatorArg args = this.buildArgs(living);
            float healthRegain = BlackDragonArmorSetEffect.HEALTH_REGAIN.getValue(args);
            living.heal(healthRegain / 10);
        }

    }

    @Override
    public void onPieceUpdate(LivingEntity livingEntity, int piece) {
        updateAttribute(livingEntity, piece);
    }

    private void updateAttribute(LivingEntity living, int piece) {
        CalculatorArg args = buildArgs(living);
        for (AttributeInstance value : living.getAttributes().supplier.instances.values()) {
            AttributeInstance instance = living.getAttribute(value.getAttribute());
            if (instance != null && instance.getAttribute().value().sentiment == Attribute.Sentiment.POSITIVE) {
                instance.removeModifier(BLACK_DRAGON_ATTRIBUTE_MULTIPLIER);
                if (piece >= 1) {
                    float multiplier = BlackDragonArmorSetEffect.ATTRIBUTE_MULTIPLIER.getValue(args);
                    if (multiplier > 0) {
                        instance.addPermanentModifier(new AttributeModifier(BLACK_DRAGON_ATTRIBUTE_MULTIPLIER, multiplier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                    }
                }
            }
        }
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        updateAttribute(livingEntity, 0);
    }
}
