package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.damageController.API.DamageControllerAPI;
import com.chen1335.damageController.API.IDamageContainerGetter;
import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public class Redemption extends LRCurioEffect {


    @Calculator
    public static final FinalCalculator SHIELD_AMOUNT = FinalCalculator.of(
            Add.of(
                    Constant.of(4),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.8F),
                                    Constant.of(1F)
                            ),
                            EntityAttributeValue.of(Attributes.MAX_HEALTH)
                    )
            )
    );

    @Calculator
    public static final FinalCalculator MAX_HEALTH_PERCENTAGE = FinalCalculator.of(Constant.of(0.25F));

    @Calculator
    public static final FinalCalculator SHIELD_LAST_TIME = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(5),
                    Constant.of(7.5F)
            )
    );

    @Calculator
    public static final FinalCalculator UNDEAD_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.2F)
            )
    );

    public Redemption(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.redemption.undead_reduce", UNDEAD_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.redemption.skill",
                MAX_HEALTH_PERCENTAGE.toPercentageComponent(tooltipFlag.hasShiftDown(), args),
                SHIELD_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                SHIELD_LAST_TIME.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.redemption.skill.desc").withColor(5592405));
        tooltipComponents.add(Component.translatable("legendary_relics.cooldown", 120).withColor(5592405));
    }


    public static void LivingDamageEvent(LivingDamageEvent.Post event) {
        LivingEntity livingEntity = event.getEntity();
        if (EquipmentEffectCooldownManager.isNotInCooldown(livingEntity, LREquipmentEffectTypes.REDEMPTION.get())) {
            LREquipmentEffectTypes.REDEMPTION.get().findBestEffect(livingEntity).ifPresent(infoHolder -> {
                CalculatorArg args = CalculatorArg.simpleArg(livingEntity, infoHolder.itemStack(), infoHolder.effect());
                if (livingEntity.getHealth() <= livingEntity.getMaxHealth() * MAX_HEALTH_PERCENTAGE.getValue(args)) {
                    ShieldAPI.addCommonTimeLimitedShield(livingEntity, SHIELD_AMOUNT.getValue(args), (int) (SHIELD_LAST_TIME.getValue(args) * 20));
                    if (livingEntity.isDeadOrDying()) {
                        livingEntity.setHealth(1);
                    }
                    EquipmentEffectCooldownManager.addCooldown(livingEntity, LREquipmentEffectTypes.REDEMPTION.get(), 120 * 20);
                }
            });
        }
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            LivingEntity livingEntity = event.getEntity();
            EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.REDEMPTION.get()).ifPresent(pair -> {
                if (attacker.getType().is(EntityTypeTags.UNDEAD)) {
                    CalculatorArg args = CalculatorArg.simpleArg(livingEntity, pair.itemStack(), pair.effect());
                    DamageControllerAPI.addMultipliedTotal((IDamageContainerGetter) event,1 -UNDEAD_REDUCE.getValue(args));
                }
            });
        }
    }
}
