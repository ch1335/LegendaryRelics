package com.chen1335.legendaryRelics.registers.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.IChargeAbleItem;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import com.chen1335.legendaryRelics.utils.LRUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class Ambush extends LRWeaponEffect {
    public static ResourceLocation BONUS_DAMAGE_ID = LegendaryRelics.id("dagger_charge");

    @Calculator
    public static final FinalCalculator DAMAGE_GAIN = FinalCalculator.of(EquipmentEffectLevelArg.of(LevelBasedValue.perLevel(3F, 0.5F)));


    public Ambush(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        LRUtil.splitAndAdd(
                tooltipComponents,
                Component.translatable("equipment_effect.legendary_relics.ambush",
                        FinalCalculator.format((float) getMaxChargeTick(itemStack) / 20, 1),
                        DAMAGE_GAIN.toPercentageComponent(tooltipFlag.hasShiftDown(), CalculatorArg.simpleArg(player, itemStack, this)),
                        Component.translatable(Attributes.ATTACK_DAMAGE.value().getDescriptionId()).withColor(5592575)
                ).withColor(0xaeaeae), getMaxToolTipWith(itemStack)
        );

    }


    public void handleReleaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        CalculatorArg arg = CalculatorArg.simpleArg(livingEntity, stack, this);
        if (livingEntity instanceof Player player) {
            double entityInteractionRange = livingEntity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
            float chargePercent = getChargePercent(stack, livingEntity);
            HitResult pick = LRUtil.pick(livingEntity, 0, entityInteractionRange);
            if (pick instanceof EntityHitResult entityHitResult && chargePercent >= 0.25) {
                AttributeMap attributes = player.getAttributes();
                AttributeInstance instance = attributes.getInstance(Attributes.ATTACK_DAMAGE);
                if (instance != null) {
                    instance.addTransientModifier(new AttributeModifier(Ambush.BONUS_DAMAGE_ID, Ambush.DAMAGE_GAIN.getValue(arg) * chargePercent, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                    entityHitResult.getEntity().invulnerableTime = 0;
                    player.attack(entityHitResult.getEntity());
                    instance.removeModifier(Ambush.BONUS_DAMAGE_ID);
                }
            }
        }
    }

    public float getChargePercent(ItemStack stack, LivingEntity entity) {
        int maxChargeTick = getMaxChargeTick(stack);
        int chargeTick = Math.min(maxChargeTick, stack.getItem().getUseDuration(stack, entity) - entity.getUseItemRemainingTicks());
        return (float) chargeTick / maxChargeTick;
    }

    public int getMaxChargeTick(ItemStack itemStack) {
        return itemStack.getItem() instanceof IChargeAbleItem chargeAbleItem ? chargeAbleItem.getMaxChargeTick(itemStack) : 60;
    }
}
