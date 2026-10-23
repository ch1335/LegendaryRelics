package com.chen1335.legendaryRelics.effectInstances;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect.BlackDragonArmorSetEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
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
        AttributeInstance instance = living.getAttribute(ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER);
        if (instance == null) {
            return;
        }

        instance.removeModifier(BLACK_DRAGON_ATTRIBUTE_MULTIPLIER);
        if (piece < 1) {
            return;
        }

        float multiplier = BlackDragonArmorSetEffect.ATTRIBUTE_MULTIPLIER.getValue(buildArgs(living));
        if (multiplier > 0) {
            instance.addTransientModifier(new AttributeModifier(BLACK_DRAGON_ATTRIBUTE_MULTIPLIER, multiplier,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        updateAttribute(livingEntity, 0);
    }
}
