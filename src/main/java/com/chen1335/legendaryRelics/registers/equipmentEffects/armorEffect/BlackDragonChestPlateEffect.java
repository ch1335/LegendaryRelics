package com.chen1335.legendaryRelics.registers.equipmentEffects.armorEffect;

import com.chen1335.damageController.API.DamageControllerAPI;
import com.chen1335.damageController.API.IDamageContainerGetter;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
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
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public class BlackDragonChestPlateEffect extends LRArmorEffect {

    @Calculator
    public static final FinalCalculator PHYSICAL_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            )
    );

    @Calculator
    public static final FinalCalculator DAMAGE_INCREASE = FinalCalculator.of(
            Add.of(
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(1F),
                                    Constant.of(1.5F)
                            ),
                            EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
                    ),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.1F),
                                    Constant.of(0.2F)
                            ),
                            EntityAttributeValue.of(Attributes.MAX_HEALTH)
                    )
            )
    );

    @Calculator
    public static final FinalCalculator SHIELD_AMOUNT = FinalCalculator.of(
            Add.of(Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.2F),
                                    Constant.of(0.3F)
                            ),
                            EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
                    ),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.3F),
                                    Constant.of(0.4F)
                            ),
                            EntityAttributeValue.of(Attributes.MAX_HEALTH)
                    )
            )
    );

    @Calculator
    public static final FinalCalculator COOL_DOWN = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(12F),
                    Constant.of(8F)
            )
    );

    @Calculator
    public static final FinalCalculator SHIELD_LAST_TIME = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(5F),
                    Constant.of(7F)
            )
    );

    public BlackDragonChestPlateEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        if (getRawEffectLevel() > 1) {
            tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_chestplate.desc.1", PHYSICAL_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        }
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.black_dragon_chestplate.desc.2",
                DAMAGE_INCREASE.toComponent(tooltipFlag.hasShiftDown(), args),
                SHIELD_LAST_TIME.toComponent(tooltipFlag.hasShiftDown(), args),
                SHIELD_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                COOL_DOWN.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(0xaeaeae), getMaxToolTipWith(itemStack));

    }


    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        EffectType<BlackDragonChestPlateEffect> effectType = LREquipmentEffectTypes.BLACK_DRAGON_CHESTPLATE_EFFECT.value();

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (effectType.isNotInCooldown(attacker)) {
                effectType.findBestEffect(attacker).ifPresent(pair -> {
                    CalculatorArg args = CalculatorArg.simpleArg(attacker, pair.itemStack(), pair.effect());
                    DamageControllerAPI.addValue((IDamageContainerGetter) event, DAMAGE_INCREASE.getValue(args));
                    ShieldAPI.addCommonDecayShield(attacker, SHIELD_AMOUNT.getValue(args), SHIELD_LAST_TIME.getInt(args) * 20);
                    EquipmentEffectCooldownManager.addCooldown(attacker, effectType, COOL_DOWN.getInt(args) * 20);
                });
            }
        }

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            effectType.findBestEffect(livingEntity).ifPresent(pair -> {
                if (event.getSource().is(Tags.DamageTypes.IS_PHYSICAL)) {
                    CalculatorArg args = CalculatorArg.simpleArg(livingEntity, pair.itemStack(), pair.effect());
                    DamageControllerAPI.addMultipliedTotal((IDamageContainerGetter) event, 1 - PHYSICAL_DAMAGE_REDUCE.getValue(args));
                }
            });
        }
    }
}
