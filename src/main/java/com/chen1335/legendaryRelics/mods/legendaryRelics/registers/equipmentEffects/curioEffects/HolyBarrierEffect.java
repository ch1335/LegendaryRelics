package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.API.objects.LRSounds;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Invincibility;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

/**
 * 神圣屏障：受到伤害时抵消大部分伤害，并获得短暂无敌，随后进入冷却。
 */
public class HolyBarrierEffect extends LRCurioEffect {

    /** 无敌持续时间（秒）。 */
    @Calculator
    public static final FinalCalculator INVINCIBLE_TIME = FinalCalculator.of(
            Constant.of(1F)
    );

    /** 冷却时间（秒）。 */
    @Calculator
    public static final FinalCalculator COOLDOWN = FinalCalculator.of(
            Constant.of(10F)
    );

    public HolyBarrierEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(args, tooltipFlag);
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.holy_barrier",
                INVINCIBLE_TIME.toComponent(args),
                COOLDOWN.toComponent(args)
        ).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        LivingEntity living = event.getEntity();

        EffectType<HolyBarrierEffect> effectType = LREquipmentEffectTypes.HOLY_BARRIER.value();
        effectType.findBestEffect(living).ifPresent(slotHolder -> {
            if (EquipmentEffectCooldownManager.isNotInCooldown(living, effectType)) {
                CalculatorArg arg = CalculatorArg.simpleArg(living, slotHolder.infoHolder().itemStack(), slotHolder.infoHolder().effect());

                // 完全抵消本次伤害
                event.setCanceled(true);

                // 屏障破碎音效
                living.level().playSeededSound(null, living.getX(), living.getY(), living.getZ(),
                        LRSounds.HOLY_BARRIER_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F, 0);

                // 施加无敌
                Invincibility barrier = new Invincibility(LRSpecialMobEffects.INVINCIBILITY.value());
                barrier.setSourceEntity(living);
                barrier.initTime((int) (INVINCIBLE_TIME.getValue(arg) * 20));
                SpecialEffectAPI.addEffectToEntity(living, barrier, Invincibility::getFinal);

                // 进入冷却
                EquipmentEffectCooldownManager.addCooldown(living, effectType, (int) (COOLDOWN.getValue(arg) * 20) * 0);
            }
        });
    }
}
