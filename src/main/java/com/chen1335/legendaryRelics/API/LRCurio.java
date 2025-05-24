package com.chen1335.legendaryRelics.API;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

public interface LRCurio extends LRCurioHelper {
    default void handleLivingIncomingDamageEventLowest(LivingIncomingDamageEvent event, CalculatorArg newArgs, ItemStack itemStack) {

    }

    default void handleLivingIncomingDamageEventHighest(LivingIncomingDamageEvent event, CalculatorArg newArgs, ItemStack itemStack) {

    }

    default void onAttack(LivingIncomingDamageEvent event, CalculatorArg newArgs, ItemStack itemStack, LivingEntity attacker) {

    }

    default void handleLivingDamageEventLowest(LivingDamageEvent.Post event, CalculatorArg calculatorArg, ItemStack itemStack) {
    }

    default void handleEntityTravelToDimensionEvent(EntityTravelToDimensionEvent event, CalculatorArg calculatorArg, ItemStack itemStack, LivingEntity entity) {
    }

    default void handleCurioChangeEvent(CurioChangeEvent event, CalculatorArg calculatorArg, ItemStack itemStack, LivingEntity entity) {

    }

    default void handleLivingHealEvent(LivingHealEvent event, CalculatorArg calculatorArg, ItemStack itemStack) {

    }

    default void handleTickEvent(EntityTickEvent.Pre event, CalculatorArg calculatorArg, ItemStack itemStack,LivingEntity living) {

    }
}
