package com.chen1335.legendaryRelics.equipmentEffects.weaponEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
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
import com.chen1335.legendaryRelics.entities.projectiles.misc.FlyingReaper;
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
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;

public class SoulEater extends LRWeaponEffect {
    public SoulEater(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public SoulEater(int level) {
        super(LREquipmentEffectTypes.SOUL_EATER.value(), level);
    }

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
                    Constant.of(1F),
                    Constant.of(1.2F)
            )
    );

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg arg = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.soul_eater", HEAL.toPercentageComponent(tooltipFlag.hasShiftDown(), arg), GAIN_ATTACK_DAMAGE_PERCENT.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
    }

    public static void LivingDeathEvent(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        ItemStack weaponItem = null;
        LivingEntity livingKiller = null;
        if (killer != null && killer.getType() == LREntityTypes.FLYING_REAPER.value()) {
            if (((FlyingReaper) killer).getOwner() instanceof LivingEntity entity) {
                livingKiller = entity;
            }
            ItemStack itemStack = killer.getWeaponItem();
            if (itemStack != null && !itemStack.isEmpty()) {
                weaponItem = itemStack;
            }
        }
        if (killer instanceof LivingEntity entity) {
            livingKiller = entity;
        }
        if (livingKiller != null) {
            TriConsumer<LivingEntity, ItemStack, SoulEater> consumer = (entity, itemStack, soulEater) -> {
                CalculatorArg arg = CalculatorArg.simpleArg(entity, itemStack, soulEater);
                entity.heal(entity.getMaxHealth() * HEAL.getValue(arg));
                if (event.getEntity().getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                    double attack = event.getEntity().getAttributeValue(Attributes.ATTACK_DAMAGE);
                    entity.getData(LRAttachmentTypes.ENTITY_DATA).getTimeLimitedAttributeBonusManager()
                            .addAttributeModifier(entity, Attributes.ATTACK_DAMAGE, new AttributeModifier(LegendaryRelics.id("soul_eater_" + event.getEntity().getId()), attack * GAIN_ATTACK_DAMAGE_PERCENT.getValue(arg), AttributeModifier.Operation.ADD_VALUE), 10 * 20);
                }
            };

            if (weaponItem != null) {
                SoulEater soulEater = EquipmentEffectAPI.getEffect(weaponItem, LREquipmentEffectTypes.SOUL_EATER.value());
                consumer.accept(livingKiller, weaponItem, soulEater);
            } else {
                LivingEntity finalLivingKiller = livingKiller;
                LREquipmentEffectTypes.SOUL_EATER.value().findBestEffect(livingKiller).ifPresent(info -> {
                    consumer.accept(finalLivingKiller, info.itemStack(), (SoulEater) info.effect());
                });
            }
        }
    }
}
