package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.API.objects.LRShieldType;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.calculator.*;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class ShieldRegeneratorEffect extends LRCurioEffectBase {
    public ShieldRegeneratorEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public ShieldRegeneratorEffect(int level) {
        this(LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.value(), level);
    }


    public static FinalCalculator COOLDOWN = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(15),
            Constant.of(10F)
    ));
    public static FinalCalculator MAX_SHIELD = FinalCalculator.of(Mul.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            ),
            EntityAttributeValue.of(Attributes.MAX_HEALTH)
    ));


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.shield_regenerator.skill.1",
                COOLDOWN.toComponent(tooltipFlag.hasShiftDown(), args),
                MAX_SHIELD.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(0xaeaeae));
    }


    @Override
    public void curioTick(ItemStack itemStack, LivingEntity wearer) {
        if (EquipmentEffectCooldownManager.isNotInCooldown(wearer, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.value())) {
            CalculatorArg args = CalculatorArg.simpleArg(wearer, itemStack, this);
            @Nullable ShieldInstanceHolder<UnitShield> instance = ShieldAPI.getShieldInstance(wearer, LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
            if (instance != null && instance.getTotalAmount() < MAX_SHIELD.getValue(args)) {
                EquipmentEffectCooldownManager.addCooldown(wearer, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.value(), COOLDOWN.getInt(args) * 20, () -> {
                    EquipmentEffectAPI.findBestEffect(wearer, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.get()).ifPresent(pair -> {
                        CalculatorArg args1 = CalculatorArg.simpleArg(wearer, pair.getFirst(), pair.getSecond());
                        instance.getShield().setAmount(MAX_SHIELD.getValue(args1));
                    });
                });
            }
        }
    }

    @Override
    public void onActive(LivingEntity entity, ItemStack itemStack) {
        if (EquipmentEffectCooldownManager.isNotInCooldown(entity, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.value())) {
            CalculatorArg args = CalculatorArg.simpleArg(entity, itemStack, this);
            @Nullable ShieldInstanceHolder<UnitShield> instance = ShieldAPI.getShieldInstance(entity, LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
            if (instance != null && instance.getTotalAmount() < MAX_SHIELD.getValue(args)) {
                EquipmentEffectCooldownManager.addCooldown(entity, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.value(), COOLDOWN.getInt(args) * 20, () -> {
                    EquipmentEffectAPI.findBestEffect(entity, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.get()).ifPresent(pair -> {
                        CalculatorArg args1 = CalculatorArg.simpleArg(entity, pair.getFirst(), pair.getSecond());
                        instance.getShield().setAmount(MAX_SHIELD.getValue(args1));
                    });
                });
            }
        }
    }

    @Override
    public void onDeActive(LivingEntity entity, ItemStack itemStack) {
        EquipmentEffectCooldownManager.addCooldown(entity, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.value(), 0);
        @Nullable ShieldInstanceHolder<UnitShield> instance = ShieldAPI.getShieldInstance(entity, LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
        if (instance != null) {
            instance.getShield().setAmount(0);
        }
    }

    @SubscribeEvent
    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        @Nullable ShieldInstanceHolder<UnitShield> instance = ShieldAPI.getShieldInstance(entity, LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
        if (instance != null) {
            EquipmentEffectAPI.findBestEffect(entity, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.get()).ifPresent(pair -> {
                CalculatorArg args = CalculatorArg.simpleArg(entity, pair.getFirst(), pair.getSecond());
                EquipmentEffectCooldownManager.addCooldown(entity, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.value(), COOLDOWN.getInt(args) * 20, () -> {
                    EquipmentEffectAPI.findBestEffect(entity, LREquipmentEffectTypes.SHIELD_REGENERATOR_EFFECT.get()).ifPresent(pair1 -> {
                        CalculatorArg args1 = CalculatorArg.simpleArg(entity, pair1.getFirst(), pair1.getSecond());
                        instance.getShield().setAmount(MAX_SHIELD.getValue(args1));
                    });
                });
            });
        }
    }
}
