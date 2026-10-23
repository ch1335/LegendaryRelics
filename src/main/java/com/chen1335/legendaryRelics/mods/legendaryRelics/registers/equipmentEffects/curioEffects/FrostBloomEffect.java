package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Slowness;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.specialEffectLib.API.SpecialEffectAddedEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.List;

/**
 * 霜华：佩戴者造成的减速效果，持续时间延长。
 *
 * <p>覆盖两类减速：
 * <ul>
 *     <li><b>原版缓慢</b>：监听 {@code MobEffectEvent.Added}，用 {@code getEffectSource()} 判断是不是佩戴者施加的，
 *         然后直接改 {@code MobEffectInstance#duration}（该字段已由 AccessTransformer 放开，
 *         项目里黑龙头盔的效果时长倍率就是同样写法）。</li>
 *     <li><b>本模组的特殊减速</b>：监听 {@link SpecialEffectAddedEvent}，直接给 {@link Slowness} 加时长。</li>
 * </ul>
 */
public class FrostBloomEffect extends LRCurioEffect {

    /** 减速效果延长的时长（秒）。 */
    @Calculator
    public static final FinalCalculator EXTEND_TIME = FinalCalculator.of(Constant.of(3F));

    public FrostBloomEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(args, tooltipFlag);
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.frost_bloom",
                EXTEND_TIME.toComponent(args)
        ).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    /** 原版缓慢：只延长佩戴者自己造成的那些。 */
    public static void MobEffectEvent$Added(MobEffectEvent.Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (!instance.getEffect().is(MobEffects.MOVEMENT_SLOWDOWN)) {
            return;
        }
        if (!(event.getEffectSource() instanceof LivingEntity applier)) {
            return;
        }
        extendByWearer(applier, instance);
    }

    /** 本模组的特殊减速：同样只延长佩戴者自己造成的。 */
    public static void SpecialEffectAddedEvent(SpecialEffectAddedEvent event) {
        if (!(event.getSpecialEffect() instanceof Slowness slowness)) {
            return;
        }
        Entity source = slowness.getSourceEntity(event.getEntity().level());
        if (!(source instanceof LivingEntity applier)) {
            return;
        }
        LREquipmentEffectTypes.FROST_BLOOM.value().findBestEffect(applier).ifPresent(holder -> {
            CalculatorArg arg = CalculatorArg.simpleArg(applier, holder.infoHolder().itemStack(), holder.infoHolder().effect());
            slowness.setTimeLeft(slowness.getTimeLeft() + (int) (EXTEND_TIME.getValue(arg) * 20));
        });
    }

    private static void extendByWearer(LivingEntity applier, MobEffectInstance instance) {
        // 无限时长（-1）不动
        if (instance.duration == -1) {
            return;
        }
        LREquipmentEffectTypes.FROST_BLOOM.value().findBestEffect(applier).ifPresent(holder -> {
            CalculatorArg arg = CalculatorArg.simpleArg(applier, holder.infoHolder().itemStack(), holder.infoHolder().effect());
            instance.duration += (int) (EXTEND_TIME.getValue(arg) * 20);
        });
    }
}
