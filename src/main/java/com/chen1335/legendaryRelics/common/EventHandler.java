package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.API.objects.*;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.items.curios.AgglomerationMalice;
import com.chen1335.legendaryRelics.items.curios.HardenedRing;
import com.chen1335.legendaryRelics.items.curios.SacredTalisman;
import com.chen1335.legendaryRelics.items.misc.AncientFragment;
import com.chen1335.legendaryRelics.items.misc.DarkGoldForgingTool;
import com.chen1335.legendaryRelics.mixins.main.CurioAttributeModifierEventInvoker;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import com.chen1335.shieldSystem.events.RegisterShieldPriorityEvent;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventHandler {
    @EventBusSubscriber(modid = LegendaryRelics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static class Game {
        @SubscribeEvent
        public static void EntityTickPre(EntityTickEvent.Pre pre) {
            if (pre.getEntity() instanceof LivingEntity living && living.hasData(LRAttachmentTypes.ENTITY_DATA)) {
                living.getData(LRAttachmentTypes.ENTITY_DATA).tick(living);
            }
        }

        @SubscribeEvent
        public static void RegisterShieldPriorityEvent(RegisterShieldPriorityEvent event) {
            event.addShieldType(LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void LivingDamageEvent(LivingDamageEvent.Post event) {
            CalculatorArg args = new CalculatorArg();
            LivingEntity entity = event.getEntity();
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, entity);
            if (entity instanceof Player player) {
                if (player.getHealth() <= player.getMaxHealth() * SacredTalisman.MAX_HEALTH_PERCENTAGE.getValue(args) && LRItems.SACRED_TALISMAN.get().isEquippedThis(player) && !player.getCooldowns().isOnCooldown(LRItems.SACRED_TALISMAN.get())) {
                    ShieldAPI.addCommonTimeLimitedShield(player, SacredTalisman.SHIELD_AMOUNT.getValue(args), (int) (SacredTalisman.SHIELD_LAST_TIME.getValue(args) * 20));
                    if (player.isDeadOrDying()) {
                        player.setHealth(1);
                    }
                    player.getCooldowns().addCooldown(LRItems.SACRED_TALISMAN.get(), 120 * 20);
                }

                if (LRItems.HARDENED_RING.get().isEquippedThis(player) && !player.getCooldowns().isOnCooldown(LRItems.HARDENED_RING.get())) {
                    player.getData(LRAttachmentTypes.ENTITY_DATA).getTimeLimitedAttributeBonusManager()
                            .addAttributeModifier(
                                    player,
                                    Attributes.ARMOR,
                                    new AttributeModifier(
                                            LegendaryRelics.id("hardened_ring_armor"),
                                            HardenedRing.ARMOR_AMOUNT.getValue(args),
                                            AttributeModifier.Operation.ADD_VALUE
                                    ),
                                    HardenedRing.TIME_KEEP.getInt(args) * 20
                            );

                    player.getCooldowns().addCooldown(LRItems.HARDENED_RING.get(), HardenedRing.COOLDOWN.getInt(args) * 20);
                }
            }

        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
            CalculatorArg args = new CalculatorArg();
            LivingEntity entity = event.getEntity();
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, entity);
            SacredTalisman sacredTalisman = LRItems.SACRED_TALISMAN.get();
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (attacker instanceof Mob) {
                    if (((Mob) attacker).getSpawnType() == MobSpawnType.SPAWNER && LRItems.AGGLOMERATION_MALICE.value().isEquippedThis(event.getEntity())) {
                        event.setAmount(event.getAmount() * AgglomerationMalice.DAMAGE_MULTIPLIER.getValue(args));
                    }
                }

                if (attacker.getType().is(EntityTypeTags.UNDEAD) && sacredTalisman.isEquippedThis(event.getEntity())) {
                    event.setAmount(event.getAmount() * (1 - SacredTalisman.UNDEAD_REDUCE.getValue(args)));
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void ItemAttributeModifierEvent(ItemAttributeModifierEvent event) {
            ItemStack itemStack = event.getItemStack();

            float i = 0;
            if (itemStack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
                i += DarkGoldForgingTool.DARK_GOLD_BOOST.getValue(CalculatorArg.emptyArg());
            }
            if (itemStack.getOrDefault(LRDataComponentTypes.ANCIENT_FRAGMENT_UPDATED, false)) {
                i += AncientFragment.ANCIENT_FRAGMENT_BOOST.getValue(CalculatorArg.emptyArg());
            }

            List<ItemAttributeModifiers.Entry> old = new ArrayList<>(event.getModifiers());
            for (ItemAttributeModifiers.Entry modifier : old) {
                boolean isNeutral = false;
                if (modifier.attribute().value().sentiment == Attribute.Sentiment.NEUTRAL) {
                    isNeutral = true;
                } else if (modifier.attribute().value().sentiment == Attribute.Sentiment.POSITIVE && modifier.modifier().amount() < 0) {
                    i = 0;
                } else if (modifier.attribute().value().sentiment == Attribute.Sentiment.NEGATIVE && modifier.modifier().amount() > 0) {
                    i = 0;
                }

                if (!isNeutral && i != 0) {
                    event.addModifier(modifier.attribute(), new AttributeModifier(LegendaryRelics.id("dark_gold_improve_" + modifier.slot().getSerializedName()), i * modifier.modifier().amount(), AttributeModifier.Operation.ADD_VALUE), modifier.slot());
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void CurioAttributeModifierEvent(CurioAttributeModifierEvent event) {
            CurioAttributeModifierEventInvoker invoker = (CurioAttributeModifierEventInvoker) event;
            ItemStack itemStack = event.getItemStack();
            float i = 0;
            if (itemStack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
                i += DarkGoldForgingTool.DARK_GOLD_BOOST.getValue(CalculatorArg.emptyArg());
            }
            if (itemStack.getOrDefault(LRDataComponentTypes.ANCIENT_FRAGMENT_UPDATED, false)) {
                i += AncientFragment.ANCIENT_FRAGMENT_BOOST.getValue(CalculatorArg.emptyArg());
            }

            Multimap<Holder<Attribute>, AttributeModifier> old = ImmutableMultimap.copyOf(invoker.lr$getModifiableMap());
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : old.entries()) {
                Holder<Attribute> attributeHolder = entry.getKey();
                AttributeModifier modifier = entry.getValue();
                boolean isNeutral = false;
                if (attributeHolder.value().sentiment == Attribute.Sentiment.NEUTRAL) {
                    isNeutral = true;
                } else if (attributeHolder.value().sentiment == Attribute.Sentiment.POSITIVE && modifier.amount() < 0) {
                    i = 0;
                } else if (attributeHolder.value().sentiment == Attribute.Sentiment.NEGATIVE && modifier.amount() > 0) {
                    i = 0;
                }
                if (!isNeutral && i != 0) {
                    event.removeModifier(attributeHolder, modifier);
                    event.addModifier(attributeHolder, new AttributeModifier(modifier.id(), (1 + i) * modifier.amount(), modifier.operation()));
                }
            }
        }

        @SubscribeEvent
        public static void AnvilUpdateEvent(AnvilUpdateEvent event) {
            ItemStack input = event.getLeft().copy();
            if (event.getLeft().getCount() == 1 && event.getRight().is(LRItems.DARK_GOLD_FORGING_TOOL) && input.getRarity() != LRRarities.DARK_GOLD.getValue()) {
                input.set(DataComponents.RARITY, LRRarities.DARK_GOLD.getValue());
                event.setOutput(input);
                event.setCost(30);
                event.setMaterialCost(1);
            } else if (event.getLeft().getCount() == 1 && event.getRight().is(LRItems.ANCIENT_FRAGMENT) && !input.getOrDefault(LRDataComponentTypes.ANCIENT_FRAGMENT_UPDATED, false)) {
                input.set(LRDataComponentTypes.ANCIENT_FRAGMENT_UPDATED, true);
                event.setOutput(input);
                event.setCost(10);
                event.setMaterialCost(1);
            }
        }
    }
}
