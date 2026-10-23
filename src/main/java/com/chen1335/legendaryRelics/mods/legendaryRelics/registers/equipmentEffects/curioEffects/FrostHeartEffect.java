package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation.Format;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Slowness;
import com.chen1335.legendaryRelics.utils.LRUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

/**
 * 冰霜之心：佩戴者受到伤害时，对造成伤害的实体施加特殊减速。
 *
 * <p>不限伤害类型：箭矢、投掷物等远程攻击会追溯到发射者；没有来源实体的环境伤害不会触发。
 * 减速走 {@link Slowness} 特殊效果，所以能和其他来源的减速一起"取最强"。
 */
public class FrostHeartEffect extends LRCurioEffect {

    /** 减速比例。 */
    @Calculator
    public static final FinalCalculator SLOWNESS = FinalCalculator.of(Constant.of(0.25F)).withFormat(Format.PERCENTAGE);

    /** 减速持续时间（秒）。 */
    @Calculator
    public static final FinalCalculator TIME = FinalCalculator.of(Constant.of(5F));

    public FrostHeartEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(args, tooltipFlag);
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.frost_heart",
                TIME.toComponent(args),
                SLOWNESS.toComponent(args)
        ).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }
        DamageSource source = event.getSource();
        // 任何伤害都算：取"造成伤害的实体"（箭矢、投掷物都会追溯到发射者）。
        // 环境伤害（摔落、着火、溺水……）没有来源实体，自然就被跳过。
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker == target) {
            return;
        }

        LREquipmentEffectTypes.FROST_HEART.value().findBestEffect(target).ifPresent(slotHolder -> {
            CalculatorArg arg = CalculatorArg.simpleArg(target, slotHolder.infoHolder().itemStack(), slotHolder.infoHolder().effect());
            // 来源传佩戴者自身：同一佩戴者反复触发只会刷新同一条减速（取最强），不会叠加
            Slowness.apply(attacker, target, SLOWNESS.getValue(arg), (int) (TIME.getValue(arg) * 20));
        });
    }
}