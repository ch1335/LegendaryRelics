package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.legendaryRelics.API.objects.LRAttributes;
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
 * <p>细雪免疫做成<b>属性</b>（{@link LRAttributes#POWDER_SNOW_WALKING}）而不是直接查饰品：
 * 细雪判定在客户端也会跑（本地玩家的移动预测），客户端查不到饰品就会按"要下沉"预测，
 * 与服务端结算互相纠正 —— 表现为站在细雪上抽搐。属性会同步给客户端，两端结论一致。
 *
 * <p>两个 mixin（{@code LivingEntityMixin} 拦冻结、{@code PowderSnowBlockMixin} 拦陷入）
 * 都只读这个属性，见 {@link #canWalkOnPowderSnow(Entity)}。
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

    @Override
    public void onActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        ResourceLocation id = slotContext.pathRL(MODIFIER_ID);

        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            CalculatorArg arg = CalculatorArg.simpleArg(entity, itemStack, this);
            speed.removeModifier(id);
            speed.addTransientModifier(new AttributeModifier(id, MOVE_SPEED.getValue(arg), AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }

        // 细雪行走：布尔属性 +1（按 BooleanAttribute 约定用 ADD_VALUE），属性会自动同步到客户端
        AttributeInstance powderSnow = entity.getAttribute(LRAttributes.POWDER_SNOW_WALKING);
        if (powderSnow != null) {
            powderSnow.removeModifier(id);
            powderSnow.addTransientModifier(new AttributeModifier(id, 1.0D, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public void onDeActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        ResourceLocation id = slotContext.pathRL(MODIFIER_ID);

        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(id);
        }
        AttributeInstance powderSnow = entity.getAttribute(LRAttributes.POWDER_SNOW_WALKING);
        if (powderSnow != null) {
            powderSnow.removeModifier(id);
        }
    }

    /**
     * 该实体能否站在细雪上（不陷入、不冻结）。
     *
     * <p>读的是<b>已同步的属性</b>，所以客户端与服务端的结论必然一致。
     */
    public static boolean canWalkOnPowderSnow(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        AttributeInstance instance = living.getAttribute(LRAttributes.POWDER_SNOW_WALKING);
        return instance != null && instance.getValue() > 0;
    }
}
