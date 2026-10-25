package com.chen1335.legendaryRelics.common;

import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.contexts.LivingHurtContext;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.contexts.LootInfoContext;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect.InfernoArmorSetEffect;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect.TwistedArmorSetEffect;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.armorEffect.BlackDragonChestPlateEffect;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.armorEffect.FallImmunity;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects.*;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.weaponEffects.Backstab;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.weaponEffects.Perforation;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.weaponEffects.SoulEater;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.LRArmorBase;
import com.chen1335.specialEffectLib.API.SpecialEffectAddedEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.function.Consumer;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class GamePlayEventHandler {
    /**
     * 神圣屏障：无敌拦截见 {@code LivingIncomingDamageEventMixin}，这里只做触发判定。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void HolyBarrierHighest(LivingIncomingDamageEvent event) {
        HolyBarrierEffect.LivingIncomingDamageEvent(event);
    }

    @SubscribeEvent
    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        InfernoArmorSetEffect.LivingIncomingDamageEvent(event);
        BlackDragonChestPlateEffect.LivingIncomingDamageEvent(event);
        TwistedArmorSetEffect.LivingIncomingDamageEvent(event);

        ShieldRegeneratorEffect.LivingIncomingDamageEvent(event);
        FallImmunity.LivingIncomingDamageEvent(event);
        HardenedEffect.LivingIncomingDamageEvent(event);
        FrostHeartEffect.LivingIncomingDamageEvent(event);


    }

    @SubscribeEvent
    public static void LivingGetAttributeAdjustmentEvent(LivingGetAttributeAdjustmentEvent event) {
        if (event.getContext() instanceof LivingHurtContext(LivingEntity target, DamageSource source, float damage)) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                BlackDragonChestPlateEffect.getAttributeAdjustment(event, source, target, attacker);
                Redemption.getAttributeAdjustment(event, source, target, attacker);
                InFireTargetDamageIncrease.getAttributeAdjustment(event, source, target, attacker);
                AgglomerationMaliceEffect.getAttributeAdjustment(event, source, target, attacker);
                Backstab.getAttributeAdjustment(event, source, target, attacker);
            }
            LRArmorBase.handleAttributeAdjustment(event, target,  source,  damage);
            FireDamageReduce.getAttributeAdjustment(event, source, target);
        } else if (event.getContext() instanceof LootInfoContext(
                LootContext lootContext, LootContextParamSet paramSet
        )) {
            if (paramSet == LootContextParamSets.ENTITY) {
                if (lootContext.getParamOrNull(LootContextParams.ATTACKING_ENTITY) instanceof LivingEntity attacker) {
                    AgglomerationMaliceEffect.getLootingAdjustment(event, lootContext, attacker);
                }
            }
        }
    }


    @SubscribeEvent
    public static void LivingEntityUseItemEvent$Stop(LivingEntityUseItemEvent.Stop event) {
        TwistedArmorSetEffect.LivingEntityUseItemEvent$Stop(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void LivingDamageEvent$Post$Lowest(LivingDamageEvent.Post event) {
        PerseveranceEffect.LivingDamageEvent(event);
        Redemption.LivingDamageEvent(event);
        ChainLightningEffect.LivingDamageEvent(event);
    }

    @SubscribeEvent
    public static void SpecialEffectAddedEvent(SpecialEffectAddedEvent event) {
        // 霜华：延长佩戴者造成的特殊减速
        FrostBloomEffect.SpecialEffectAddedEvent(event);
    }

    @SubscribeEvent
    public static void EntityTickPre(EntityTickEvent.Pre event) {
        // 寒域：群系会随走动改变，装备状态不会，所以要周期性检查
        ColdDomainEffect.EntityTickEvent(event);
    }

    @SubscribeEvent
    public static void LivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {

    }

    @SubscribeEvent
    public static void EntityJoinLevelEvent(EntityJoinLevelEvent event) {
        TwistedArmorSetEffect.EntityJoinLevelEvent(event);
        Perforation.EntityJoinLevelEvent(event);
    }

    @SubscribeEvent
    public static void BlockBreakEvent(BlockEvent.BreakEvent event) {
        OreCollectorEffect.countOres(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void LivingDeathEvent$Lowest(LivingDeathEvent event) {
        SoulEater.LivingDeathEvent(event);
        InfernoArmorSetEffect.LivingDeathEvent(event);
        GameTaskEffect.LivingDeathEvent(event);
    }

    @SubscribeEvent
    public static void EntityTravelToDimensionEvent(EntityTravelToDimensionEvent event) {
        AttributeBoostInNether.EntityTravelToDimensionEvent(event);
    }

    @SubscribeEvent
    public static void PlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        GameTaskEffect.PlayerChangedDimensionEvent(event);
    }

    @SubscribeEvent
    public static void LivingHealEvent(LivingHealEvent event) {
        HealIncreaseEffect.LivingHealEvent(event);
    }

    private static <T extends ICancellableEvent> void applyEvent(T event, Consumer<T> consumer){
        if (event.isCanceled()) {
            return;
        }
        consumer.accept(event);
    }
}
