package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.API.objects.LRShieldType;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import com.chen1335.shieldSystem.shieldSystem.ShieldInstanceHolder;
import com.chen1335.shieldSystem.shieldSystem.UnitShield;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShieldRegeneratorEffect extends LRCurioEffectBase {
    public ShieldRegeneratorEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public ShieldRegeneratorEffect(int level) {
        this(LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.value(), level);
    }

    @Calculator
    public static final FinalCalculator COOLDOWN = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(15),
            Constant.of(10F)
    ));

    @Calculator
    public static final FinalCalculator MAX_SHIELD = FinalCalculator.of(Add.of(
            DarkGoldUpdateArg.of(
                    Constant.of(2),
                    Constant.of(4)
            ),
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(0.1F),
                            Constant.of(0.15F)
                    ),
                    EntityAttributeValue.of(Attributes.MAX_HEALTH)
            )
    ));


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.shield_regenerator_effect",
                COOLDOWN.toComponent(tooltipFlag.hasShiftDown(), args),
                MAX_SHIELD.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(0xaeaeae));
    }


    @Override
    public void curioTick(ItemStack itemStack, LivingEntity wearer) {
        if (isNotInCooldown(wearer)) {
            CalculatorArg args = CalculatorArg.simpleArg(wearer, itemStack, this);
            @Nullable ShieldInstanceHolder<UnitShield> instance = ShieldAPI.getShieldInstance(wearer, LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
            if (instance != null && instance.getTotalAmount() < MAX_SHIELD.getValue(args)) {
                addCooldown(wearer, COOLDOWN.getInt(args) * 20, () -> {
                    this.findBestEffect(wearer).ifPresent(pair -> {
                        CalculatorArg args1 = CalculatorArg.simpleArg(wearer, pair.itemStack(), pair.effect());
                        instance.getShield().setAmount(MAX_SHIELD.getValue(args1));
                    });
                });
            }
        }
    }

    @Override
    public void onActive(LivingEntity entity, ItemStack itemStack) {
        if (isNotInCooldown(entity)) {
            CalculatorArg args = CalculatorArg.simpleArg(entity, itemStack, this);
            @Nullable ShieldInstanceHolder<UnitShield> instance = ShieldAPI.getShieldInstance(entity, LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
            if (instance != null && instance.getTotalAmount() < MAX_SHIELD.getValue(args)) {
                addCooldown(entity, COOLDOWN.getInt(args) * 20, () -> {
                    findBestEffect(entity).ifPresent(pair -> {
                        CalculatorArg args1 = CalculatorArg.simpleArg(entity, pair.itemStack(), pair.effect());
                        instance.getShield().setAmount(MAX_SHIELD.getValue(args1));
                    });
                });
            }
        }
    }

    @Override
    public void onDeActive(LivingEntity entity, ItemStack itemStack) {
        if (!isNotInCooldown(entity)) {
            this.addCooldown(entity, 0);
        }
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        EffectType<ShieldRegeneratorEffect> effectType = LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.get();
        @Nullable ShieldInstanceHolder<UnitShield> instance = ShieldAPI.getShieldInstance(entity, LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
        if (instance != null) {
            effectType.findBestEffect(entity).ifPresent(pair -> {
                pair.effect().addCooldown(entity, COOLDOWN.getInt(CalculatorArg.simpleArg(entity, pair.itemStack(), pair.effect())) * 20, () -> {
                    effectType.findBestEffect(entity).ifPresent(pair1 -> {
                        instance.getShield().setAmount(MAX_SHIELD.getValue(CalculatorArg.simpleArg(entity, pair1.itemStack(), pair1.effect())));
                    });
                });
            });
        }
    }
}
