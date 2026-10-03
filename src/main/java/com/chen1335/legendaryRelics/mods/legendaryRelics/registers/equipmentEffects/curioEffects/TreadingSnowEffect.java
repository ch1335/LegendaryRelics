package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.utils.LRUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 踏雪：免疫细雪的冻结与陷入，并提升移动速度。
 *
 * <p>细雪免疫靠两个 mixin（{@code LivingEntityCanFreezeMixin} 拦冻结、
 * {@code PowderSnowBlockMixin} 拦陷入），它们都通过
 * {@link #preventsPowderSnowSinking(Entity)} 反查这个效果，和 {@code Invincibility} 同一套路。
 */
public class TreadingSnowEffect extends LRCurioEffect {

    private static final ResourceLocation MODIFIER_ID = LegendaryRelics.id("treading_snow");

    /** 移动速度提升比例。 */
    @Calculator
    public static final FinalCalculator MOVE_SPEED = FinalCalculator.of(Constant.of(0.1F));

    public TreadingSnowEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.treading_snow",
                MOVE_SPEED.toComponent(tooltipFlag.hasShiftDown(), args, Style.EMPTY, 100, "%s%%")
        ).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    private static AttributeInstance speedOf(LivingEntity entity) {
        return entity.getAttribute(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public void onActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        AttributeInstance speed = speedOf(entity);
        if (speed == null) {
            return;
        }
        CalculatorArg arg = CalculatorArg.simpleArg(entity, itemStack, this);
        speed.removeModifier(slotContext.pathRL(MODIFIER_ID));
        speed.addTransientModifier(new AttributeModifier(slotContext.pathRL(MODIFIER_ID), MOVE_SPEED.getValue(arg), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }

    @Override
    public void onDeActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        AttributeInstance speed = speedOf(entity);
        if (speed != null) {
            speed.removeModifier(slotContext.pathRL(MODIFIER_ID));
        }
    }

    /**
     * 该实体是否免疫细雪的冻结与陷入（供两个 mixin 调用）。
     */
    public static boolean preventsPowderSnowSinking(Entity entity) {
        return entity instanceof LivingEntity living
                && LREquipmentEffectTypes.TREADING_SNOW.value().findBestEffect(living).isPresent();
    }
}
