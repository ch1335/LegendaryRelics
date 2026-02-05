package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;


import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
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

public class AttributeBoostInNether extends LRCurioEffectBase {

    @Calculator
    public static final FinalCalculator ATTRIBUTE_BOOST = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(LevelBasedValue.perLevel(0.025F), 3)
    ));

    @Override
    public int getDarkGoldLevelAdd() {
        return getRawEffectLevel();
    }

    public AttributeBoostInNether(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public AttributeBoostInNether(int level) {
        this(LREquipmentEffectTypes.ATTRIBUTE_BOOST_IN_NETHER.value(), level);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.attribute_boost_in_nether", ATTRIBUTE_BOOST.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

    private void addAttribute(LivingEntity living, ItemStack itemStack) {
        CalculatorArg arg = CalculatorArg.simpleArg(living, itemStack, this);
        AttributeModifyHelper.addAllPositive(living, getModifierId(), ATTRIBUTE_BOOST.getValue(arg), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    }

    private void removeAttribute(LivingEntity living, ItemStack itemStack) {
        AttributeModifyHelper.removeAllPositive(living, getModifierId());
    }

    @Override
    public void onDeActive(LivingEntity entity, ItemStack itemStack) {
        if (entity.level().dimension().equals(ServerLevel.NETHER)) {
            removeAttribute(entity, itemStack);
        }
    }

    @Override
    public void onActive(LivingEntity entity, ItemStack itemStack) {
        if (entity.level().dimension().equals(ServerLevel.NETHER)) {
            addAttribute(entity, itemStack);
        }
    }

    public ResourceLocation getModifierId() {
        return LegendaryRelics.id("nether_multiplier_" + this.hashCode());
    }

    public static void EntityTravelToDimensionEvent(EntityTravelToDimensionEvent event) {

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.ATTRIBUTE_BOOST_IN_NETHER.value()).ifPresent(pair -> {
                AttributeBoostInNether effect = pair.effect();
                if (event.getDimension().equals(ServerLevel.NETHER)) {
                    effect.addAttribute(livingEntity, pair.itemStack());
                } else {
                    effect.removeAttribute(livingEntity, pair.itemStack());
                }
            });
        }
    }
}
