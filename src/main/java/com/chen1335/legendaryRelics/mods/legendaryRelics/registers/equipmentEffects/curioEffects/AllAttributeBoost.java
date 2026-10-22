package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.util.ComponentUtil;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.LRBaseEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AllAttributeBoost extends LRBaseEffect {
    public static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "all_attribute_boost");

    @Calculator
    public static final FinalCalculator AMOUNT = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.025F),
            Constant.of(0.035F)
    ));

    public AllAttributeBoost(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        Component boost = ComponentUtil.Attributes.attribute(ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER,
                AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args, Style.EMPTY, 100, "%s%%"),
                "+");
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.all_attribute_boost", boost)
                .withStyle(ChatFormatting.GOLD));
    }


    @Override
    public void onActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        CalculatorArg arg = CalculatorArg.simpleArg(entity, itemStack, this);
        AttributeInstance attribute = entity.getAttribute(ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER);
        if (attribute == null) {
            return;
        }

        attribute.removeModifier(slotContext.pathRL(MODIFIER_ID));
        float boost = AMOUNT.getValue(arg);
        if (boost > 0) {
            attribute.addTransientModifier(new AttributeModifier(slotContext.pathRL(MODIFIER_ID), boost,
                    AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public void onDeActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        AttributeInstance attribute = entity.getAttribute(ArcanumAttributes.ALL_ATTRIBUTE_MULTIPLIER);
        if (attribute != null) {
            attribute.removeModifier(slotContext.pathRL(MODIFIER_ID));
        }
    }
}
