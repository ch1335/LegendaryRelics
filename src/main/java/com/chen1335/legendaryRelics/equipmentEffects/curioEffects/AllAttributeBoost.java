package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.equipmentEffects.LRBaseEffect;
import com.chen1335.legendaryRelics.utils.AttributeModifyHelper;
import com.chen1335.legendaryRelics.utils.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AllAttributeBoost extends LRBaseEffect {
    private ResourceLocation modifierId = Util.randomLocation(10);

    @Calculator
    public static final FinalCalculator AMOUNT = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.025F),
            Constant.of(0.035F)
    ));

    public AllAttributeBoost(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.all_attribute_boost", AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args)));
    }

    @Override
    public CompoundTag saveSimpleData() {
        CompoundTag compoundTag = super.saveSimpleData();
        compoundTag.putString("modifierId", modifierId.toString());
        return compoundTag;
    }

    @Override
    public void loadSimpleData(CompoundTag nbt) {
        super.loadSimpleData(nbt);
        modifierId = ResourceLocation.parse(nbt.getString("modifierId"));
    }

    @Override
    public void onActive(LivingEntity entity, ItemStack itemStack) {
        CalculatorArg arg = CalculatorArg.simpleArg(entity, itemStack, this);
        AttributeModifyHelper.addAllPositive(entity, modifierId, AMOUNT.getValue(arg), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public void onDeActive(LivingEntity entity, ItemStack itemStack) {
        AttributeModifyHelper.removeAllPositive(entity, modifierId);
    }
}
