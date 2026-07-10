package com.chen1335.legendaryRelics.registers.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.FlyingReaper;
import com.chen1335.legendaryRelics.utils.LRUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;

public class SoulEater extends LRWeaponEffect {

    @Calculator
    public static final FinalCalculator HEAL = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            )
    );

    @Calculator
    public static final FinalCalculator GAIN_ATTACK_DAMAGE_PERCENT = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.5F),
                    Constant.of(0.7F)
            )
    );

    public SoulEater(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg arg = CalculatorArg.simpleArg(player, itemStack, this);
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.soul_eater", HEAL.toPercentageComponent(tooltipFlag.hasShiftDown(), arg), GAIN_ATTACK_DAMAGE_PERCENT.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    public static void LivingDeathEvent(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        ItemStack weaponItem = ItemStack.EMPTY;
        LivingEntity livingKiller = null;
        if (killer != null && killer.getType() == LREntityTypes.FLYING_REAPER.value()) {
            FlyingReaper flyingReaper = (FlyingReaper) killer;
            if (flyingReaper.getOwner() instanceof LivingEntity entity) {
                livingKiller = entity;
            }
            weaponItem = killer.getWeaponItem();
        } else if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            livingKiller = attacker;
            weaponItem = attacker.getWeaponItem();
        }

        if (livingKiller != null && !weaponItem.isEmpty()) {
            SoulEater soulEater = EquipmentEffectAPI.getEffect(weaponItem, LREquipmentEffectTypes.SOUL_EATER.value());
            CalculatorArg arg = CalculatorArg.simpleArg(livingKiller, weaponItem, soulEater);
            livingKiller.heal(livingKiller.getMaxHealth() * HEAL.getValue(arg));
            if (event.getEntity().getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                double attack = event.getEntity().getAttributeValue(Attributes.ATTACK_DAMAGE);
                livingKiller.getData(LRAttachmentTypes.ENTITY_DATA).getTimeLimitedAttributeBonusManager()
                        .addAttributeModifier(livingKiller, Attributes.ATTACK_DAMAGE, new AttributeModifier(LegendaryRelics.id("soul_eater_" + event.getEntity().getId()), attack * GAIN_ATTACK_DAMAGE_PERCENT.getValue(arg), AttributeModifier.Operation.ADD_VALUE), 10 * 20);
            }
        }
    }

}
