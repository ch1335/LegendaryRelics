package com.chen1335.legendaryRelics.common;

import com.chen1335.equipmentEffectLib.events.SetItemSetsEffectEvent;
import com.chen1335.legendaryRelics.API.objects.*;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.armorSetEffect.BlackDragonArmorSetEffect;
import com.chen1335.legendaryRelics.attachmentDatas.LREntityData;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.items.armor.BlackDragonArmor;
import com.chen1335.legendaryRelics.items.armor.BlackDragonHelmet;
import com.chen1335.legendaryRelics.items.armor.BlackDragonLeggings;
import com.chen1335.legendaryRelics.items.misc.AncientFragment;
import com.chen1335.legendaryRelics.items.misc.DarkGoldForgingTool;
import com.chen1335.legendaryRelics.mixins.main.CurioAttributeModifierEventInvoker;
import com.chen1335.legendaryRelics.network.EffectCooldownPack;
import com.chen1335.legendaryRelics.network.SetsInfoPack;
import com.chen1335.shieldSystem.events.RegisterShieldPriorityEvent;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventHandler {
    @EventBusSubscriber(modid = LegendaryRelics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static class Game {

        @SubscribeEvent
        public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (ModList.get().isLoaded("patchouli")) {
                LREntityData entityData = event.getEntity().getData(LRAttachmentTypes.ENTITY_DATA);
                if (!entityData.hasGiveBook) {
                    event.getEntity().addItem(ItemModBook.forBook(LegendaryRelics.id("legendary_relics_book")));
                    entityData.hasGiveBook = true;
                }
            }
        }

        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            event.getEntity().setData(LRAttachmentTypes.ENTITY_DATA, event.getOriginal().getData(LRAttachmentTypes.ENTITY_DATA));
        }

        @SubscribeEvent
        public static void setItemSetsEffect(SetItemSetsEffectEvent event) {
            event.set(LRItems.BLACK_DRAGON_HELMET.asItem(), LRSetsEffects.BLACK_DRAGON_ARMOR.value());
            event.set(LRItems.BLACK_DRAGON_CHEST_PLATE.asItem(), LRSetsEffects.BLACK_DRAGON_ARMOR.value());
            event.set(LRItems.BLACK_DRAGON_LEGGINGS.asItem(), LRSetsEffects.BLACK_DRAGON_ARMOR.value());
            event.set(LRItems.BLACK_DRAGON_BOOTS.asItem(), LRSetsEffects.BLACK_DRAGON_ARMOR.value());
        }

        @SubscribeEvent
        public static void heal(LivingHealEvent event) {
            CalculatorArg arg = CalculatorArg.emptyArg();
            CalculatorArg.ArgType.THIS_ENTITY.putArg(arg, event.getEntity());
            LRItems.BLACK_DRAGON_LEGGINGS.get().runIfEquippedThis(event.getEntity(), arg, (itemStack, arg1) -> event.setAmount(event.getAmount() * (1 + BlackDragonLeggings.HEAL_INCREASE.getValue(arg1))));
        }

        @SubscribeEvent
        public static void MobEffectEvent$Added(MobEffectEvent.Added event) {
            ItemStack headArmor = event.getEntity().getItemBySlot(EquipmentSlot.HEAD);
            if (headArmor.getItem() == LRItems.BLACK_DRAGON_HELMET.get()) {
                CalculatorArg arg = CalculatorArg.emptyArg();
                CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, headArmor);
                MobEffectInstance effectInstance = event.getEffectInstance();
                if (effectInstance != null) {
                    if (effectInstance.getEffect().value().getCategory() != MobEffectCategory.HARMFUL) {
                        effectInstance.duration = (int) (effectInstance.duration * BlackDragonHelmet.GOOD_EFFECT_TIME_MULTIPLIER.getValue(arg));
                    } else {
                        effectInstance.duration = (int) (effectInstance.duration * BlackDragonHelmet.BAD_EFFECT_TIME_MULTIPLIER.getValue(arg));
                    }
                }
            }
        }


        @SubscribeEvent
        public static void EntityTickPre(EntityTickEvent.Pre event) {
            if (event.getEntity() instanceof LivingEntity living && !living.level().isClientSide) {
                if (living.hasData(LRAttachmentTypes.ENTITY_DATA)) {
                    living.getData(LRAttachmentTypes.ENTITY_DATA).tick(living);
                }
            }
        }

        @SubscribeEvent
        public static void RegisterShieldPriorityEvent(RegisterShieldPriorityEvent event) {
            event.addShieldType(LRShieldType.SHIELD_REGENERATOR_SHIELD.get());
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void LivingDamageEvent(LivingDamageEvent.Post event) {

        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void LivingIncomingDamageEventLowest(LivingIncomingDamageEvent event) {
            CalculatorArg args = new CalculatorArg();
            LivingEntity entity = event.getEntity();
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, entity);
            for (ItemStack armorSlot : entity.getArmorSlots()) {
                if (armorSlot.getItem() instanceof BlackDragonArmor blackDragonArmor) {
                    CalculatorArg args1 = args.copy();
                    CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args1, armorSlot);
                    blackDragonArmor.handleDamageReduce(event, args1, armorSlot);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void LivingIncomingDamageEventHighest(LivingIncomingDamageEvent event) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (attacker instanceof Player playerAttacker) {
                    CalculatorArg args = new CalculatorArg();
                    CalculatorArg.ArgType.THIS_ENTITY.putArg(args, attacker);
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

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class Mod {
        @SubscribeEvent
        public static void RegisterPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(EffectCooldownPack.TYPE, EffectCooldownPack.STREAM_CODEC, EffectCooldownPack::handler);
            registrar.playToClient(SetsInfoPack.TYPE, SetsInfoPack.STREAM_CODEC, SetsInfoPack::handler);

        }
    }
}
