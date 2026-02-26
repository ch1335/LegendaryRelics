package com.chen1335.legendaryRelics.effectInstances;

import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.armorSetEffect.InfernoArmorSetEffect;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class InfernoEffectInstance extends EffectInstance {
    private static final ResourceLocation ATTACK_RANGE = LegendaryRelics.id("inferno.attack_range");
    private static final ResourceLocation ATTACK_DAMAGE = LegendaryRelics.id("inferno.attack_damage");
    public int stack = 0;
    public int coolDown = 0;
    public int keepTime = 0;
    private int oldStack = 0;

    private int tickCounter = 0;

    public InfernoEffectInstance(int piece) {
        super(piece);
    }

    @Override
    public void tick(LivingEntity living) {
        coolDown = Math.max(coolDown - 1, 0);
        keepTime = Math.max(keepTime - 1, 0);
        if (oldStack != stack) {
            oldStack = stack;
            updateAttribute(living, piece);
        }


        if (keepTime == 0) {
            tickCounter++;
            if (tickCounter % 20 == 0) {
                addStack(-1);
                tickCounter = 0;
            }
        } else {
            tickCounter = 0;
        }
    }

    @Override
    public void onPieceUpdate(LivingEntity livingEntity, int piece) {
        updateAttribute(livingEntity, piece);
    }

    private void updateAttribute(LivingEntity living, int piece) {
        CalculatorArg args = buildArgs(living);
        AttributeMap attributes = living.getAttributes();
        AttributeInstance attackRange = attributes.getInstance(Attributes.ENTITY_INTERACTION_RANGE);
        AttributeInstance attackDamage = attributes.getInstance(Attributes.ATTACK_DAMAGE);
        if (attackRange != null) {
            attackRange.removeModifier(ATTACK_RANGE);
            if (piece >= 2) {
                attackRange.addPermanentModifier(new AttributeModifier(ATTACK_RANGE, stack * InfernoArmorSetEffect.ATTACK_RANGE_PER_STACK.getValue(args), AttributeModifier.Operation.ADD_VALUE));
            }
        }

        if (attackDamage != null) {
            attackDamage.removeModifier(ATTACK_DAMAGE);
            if (piece >= 2) {
                attackDamage.addPermanentModifier(new AttributeModifier(ATTACK_DAMAGE, stack * InfernoArmorSetEffect.DAMAGE_PER_STACK.getValue(args), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    public void addStack(int add) {
        this.stack = Math.max(Math.min(10, stack + add), 0);
    }

    public int getStack() {
        return stack;
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        updateAttribute(livingEntity, 0);
    }
}
