package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;


import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.slotEffectManagers.ISlotContext;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import com.chen1335.legendaryRelics.utils.AttributeModifyHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;

import java.util.List;

public class AttributeBoostInNether extends LRCurioEffect {
    public static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "attribute_boost_in_nether");

    @Calculator
    public static final FinalCalculator ATTRIBUTE_BOOST = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(LevelBasedValue.perLevel(0.025F), 3)
    ));

    public AttributeBoostInNether(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public int getDarkGoldLevelAdd() {
        return getRawEffectLevel();
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.attribute_boost_in_nether", ATTRIBUTE_BOOST.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

    private void addAttribute(ISlotContext slotContext, LivingEntity living, ItemStack itemStack) {
        CalculatorArg arg = CalculatorArg.simpleArg(living, itemStack, this);
        AttributeModifyHelper.addAllPositive(living, modifierId, ATTRIBUTE_BOOST.getValue(arg), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    }

    private void removeAttribute(LivingEntity living, ItemStack itemStack) {
        AttributeModifyHelper.removeAllPositive(living, modifierId);
    }

    @Override
    public void onDeActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        if (entity.level().dimension().equals(ServerLevel.NETHER)) {
            removeAttribute(entity, itemStack);
        }
    }

    @Override
    public void onActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
        if (entity.level().dimension().equals(ServerLevel.NETHER)) {
            addAttribute(slotContext,entity, itemStack);
        }
    }

    public static void EntityTravelToDimensionEvent(EntityTravelToDimensionEvent event) {

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.ATTRIBUTE_BOOST_IN_NETHER.value()).ifPresent(pair -> {
                AttributeBoostInNether effect = pair.effect();
                if (event.getDimension().equals(ServerLevel.NETHER)) {
                    effect.addAttribute(slotContext, livingEntity, pair.itemStack());
                } else {
                    effect.removeAttribute(livingEntity, pair.itemStack());
                }
            });
        }
    }
}
