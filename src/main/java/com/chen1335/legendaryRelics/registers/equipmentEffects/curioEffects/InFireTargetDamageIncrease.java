package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.damageController.API.DamageControllerAPI;
import com.chen1335.damageController.API.IDamageContainerGetter;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EquipmentEffectLevelArg;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public class InFireTargetDamageIncrease extends LRCurioEffect {



    @Calculator
    public static final FinalCalculator DAMAGE_INCREASE = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(LevelBasedValue.perLevel(0.1F, 0.05F), 3)
    ));

    public InFireTargetDamageIncrease(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.in_fire_target_damage_increase", DAMAGE_INCREASE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        if (event.getEntity().isOnFire() && event.getSource().getEntity() instanceof LivingEntity attacker) {
            LREquipmentEffectTypes.IN_FIRE_TARGET_DAMAGE_INCREASE.value().findBestEffect(attacker).ifPresent(slotHolder -> {
                DamageControllerAPI.addMultipliedBase((IDamageContainerGetter) event, DAMAGE_INCREASE.getValue(CalculatorArg.simpleArg(attacker, slotHolder.infoHolder().itemStack(), slotHolder.infoHolder().effect())));
            });
        }
    }
}
