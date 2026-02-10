package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.armorSetEffect.BlackDragonArmorSetEffect;
import com.chen1335.legendaryRelics.armorSetEffect.InfernoArmorSetEffect;
import com.chen1335.legendaryRelics.armorSetEffect.TwistedArmorSetEffect;
import com.chen1335.legendaryRelics.equipmentEffects.armorEffect.BlackDragonChestPlateEffect;
import com.chen1335.legendaryRelics.equipmentEffects.armorEffect.FallImmunity;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.*;
import com.chen1335.legendaryRelics.equipmentEffects.weaponEffects.SoulEater;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class GamePlayEventHandler {
    @SubscribeEvent
    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        InfernoArmorSetEffect.LivingIncomingDamageEvent(event);
        BlackDragonChestPlateEffect.LivingIncomingDamageEvent(event);
        TwistedArmorSetEffect.LivingIncomingDamageEvent(event);

        ShieldRegeneratorEffect.LivingIncomingDamageEvent(event);
        InFireTargetDamageIncrease.LivingIncomingDamageEvent(event);
        FireDamageReduce.LivingIncomingDamageEvent(event);
        AgglomerationMaliceEffect.LivingIncomingDamageEvent(event);
        FallImmunity.LivingIncomingDamageEvent(event);
        HardenedEffect.LivingIncomingDamageEvent(event);
        Redemption.LivingIncomingDamageEvent(event);
    }

    @SubscribeEvent
    public static void LivingEntityUseItemEvent$Stop(LivingEntityUseItemEvent.Stop event){
        TwistedArmorSetEffect.LivingEntityUseItemEvent$Stop(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void LivingDamageEvent$Post$Lowest(LivingDamageEvent.Post event) {
        PerseveranceEffect.LivingDamageEvent(event);
        Redemption.LivingDamageEvent(event);
    }

    @SubscribeEvent
    public static void EntityTickPre(EntityTickEvent.Pre event) {

    }

    @SubscribeEvent
    public static void LivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {

    }

    @SubscribeEvent
    public static void BlockBreakEvent(BlockEvent.BreakEvent event) {
        OreCollectorEffect.countOres(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void LivingDeathEvent$Lowest(LivingDeathEvent event) {
        if (!event.isCanceled()) {
            SoulEater.LivingDeathEvent(event);
            InfernoArmorSetEffect.LivingDeathEvent(event);
        }
    }

    @SubscribeEvent
    public static void EntityTravelToDimensionEvent(EntityTravelToDimensionEvent event) {
        AttributeBoostInNether.EntityTravelToDimensionEvent(event);
    }

    @SubscribeEvent
    public static void LivingHealEvent(LivingHealEvent event) {
        HealIncreaseEffect.LivingHealEvent(event);
    }
}
