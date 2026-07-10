package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public class HardenedEffect extends LRCurioEffect {


    @Calculator
    public static final FinalCalculator ARMOR_AMOUNT = FinalCalculator.of(
            Add.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(2F),
                            Constant.of(4F)
                    ),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.50F),
                                    Constant.of(0.75F)
                            ),
                            EntityAttributeValue.of(Attributes.ARMOR)
                    )
            )
    );

    @Calculator
    public static final FinalCalculator TIME_KEEP = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(5),
            Constant.of(7.5F)
    ));

    @Calculator
    public static final FinalCalculator COOLDOWN = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(15),
            Constant.of(12.5F)
    ));

    public HardenedEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.hardened_effect",
                ARMOR_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                TIME_KEEP.toComponent(tooltipFlag.hasShiftDown(), args),
                COOLDOWN.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(0xaeaeae));
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        if (!event.getEntity().level().isClientSide) {
            LREquipmentEffectTypes.HARDENED_EFFECT.value().findBestEffect(event.getEntity()).ifPresent(slotHolder -> {
                LivingEntity living = event.getEntity();
                CalculatorArg calculatorArg = CalculatorArg.simpleArg(living, slotHolder.infoHolder().itemStack(), slotHolder.infoHolder().effect());
                if (EquipmentEffectCooldownManager.isNotInCooldown(living, LREquipmentEffectTypes.HARDENED_EFFECT.value())) {
                    living.getData(LRAttachmentTypes.ENTITY_DATA).getTimeLimitedAttributeBonusManager()
                            .addAttributeModifier(
                                    living,
                                    Attributes.ARMOR,
                                    new AttributeModifier(
                                            LegendaryRelics.id("hardened_ring_armor"),
                                            ARMOR_AMOUNT.getValue(calculatorArg),
                                            AttributeModifier.Operation.ADD_VALUE
                                    ),
                                    TIME_KEEP.getInt(calculatorArg) * 20
                            );
                    EquipmentEffectCooldownManager.addCooldown(living, LREquipmentEffectTypes.HARDENED_EFFECT.value(), COOLDOWN.getInt(calculatorArg) * 20);
                }
            });

        }
    }
}
