package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.damageController.API.DamageControllerAPI;
import com.chen1335.damageController.API.IDamageContainerGetter;
import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

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
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.fire_damage_reduce", FIRE_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        EquipmentEffectAPI.findBestEffect(event.getEntity(), LREquipmentEffectTypes.FIRE_DAMAGE_REDUCE.value()).ifPresent(pair -> {
            if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
                DamageControllerAPI.addMultipliedTotal((IDamageContainerGetter) event, -FIRE_DAMAGE_REDUCE.getValue(CalculatorArg.simpleArg(event.getEntity(), pair.itemStack(), pair.effect())));
            }
        });
    }
}
