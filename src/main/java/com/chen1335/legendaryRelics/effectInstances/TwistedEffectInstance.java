package com.chen1335.legendaryRelics.effectInstances;

import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.armorSetEffect.TwistedArmorSetEffect;
import com.chen1335.legendaryRelics.attachmentDatas.LRProjectileData;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class TwistedEffectInstance extends EffectInstance {
    private static final ResourceLocation DRAW_SPEED = LegendaryRelics.id("twisted.attack_range");
    private static final ResourceLocation ARROW_DAMAGE = LegendaryRelics.id("twisted.attack_damage");
    public int stack = 0;
    public int coolDown = 0;
    public int keepTime = 0;
    private int oldStack = 0;

    private int tickCounter = 0;

    public boolean isDoingAdditionShoot = false;

    public TwistedEffectInstance(int piece) {
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
        AttributeInstance drawSpeed = attributes.getInstance(AttributesGetter.drawSpeed());
        AttributeInstance arrowDamage = attributes.getInstance(AttributesGetter.arrowDamage());
        if (drawSpeed != null) {
            drawSpeed.removeModifier(DRAW_SPEED);
            if (piece >= 2) {
                drawSpeed.addPermanentModifier(new AttributeModifier(DRAW_SPEED, stack * TwistedArmorSetEffect.DRAW_SPEED_PER_STACK.getValue(args), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }

        if (arrowDamage != null) {
            arrowDamage.removeModifier(ARROW_DAMAGE);
            if (piece >= 2) {
                arrowDamage.addPermanentModifier(new AttributeModifier(ARROW_DAMAGE, stack * TwistedArmorSetEffect.ARROW_DAMAGE_PER_STACK.getValue(args), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    public void addStack(int add) {
        this.stack = Math.max(Math.min(10, stack + add), 0);
    }

    public int getDoomLevel() {
        return stack;
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        updateAttribute(livingEntity, 0);
    }

    public void modifyArrow(AbstractArrow arrow, LivingEntity owner) {
        if (isDoingAdditionShoot) {
            LRProjectileData data = arrow.getData(LRAttachmentTypes.PROJECTILE_DATA.get());
            data.damageMul = data.damageMul * TwistedArmorSetEffect.ADDITION_ARROW_BASE_DAMAGE.getValue(buildArgs(owner));
        }
    }
}
