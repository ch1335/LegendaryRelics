package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class FireDamageReduce extends LRCurioEffect {


    @Calculator
    public static final FinalCalculator FIRE_DAMAGE_REDUCE = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.35F),
            Constant.of(0.5F)
    ));

    public FireDamageReduce(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.fire_damage_reduce", FIRE_DAMAGE_REDUCE.toComponent(tooltipFlag.hasShiftDown(), args, Style.EMPTY, 100, "%s%%")).withColor(0xaeaeae));
    }

    public static void getAttributeAdjustment(LivingGetAttributeAdjustmentEvent event, DamageSource source, LivingEntity target) {
        if (source.is(DamageTypeTags.IS_FIRE)) {
            EquipmentEffectAPI.findBestEffect(target, LREquipmentEffectTypes.FIRE_DAMAGE_REDUCE.value()).ifPresent(pair -> {
                event.getAdjustment(target, ArcanumAttributes.DAMAGE_TAKEN_MULTIPLIER).add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, -FIRE_DAMAGE_REDUCE.getValue(CalculatorArg.simpleArg(target, pair.infoHolder().itemStack(), pair.infoHolder().effect())));
            });
        }
    }
}
