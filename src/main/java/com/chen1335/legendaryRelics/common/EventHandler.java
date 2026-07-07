package com.chen1335.legendaryRelics.common;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.events.SetItemSetsEffectEvent;
import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.chen1335.legendaryRelics.API.IRenderArrowBow;
import com.chen1335.legendaryRelics.API.objects.*;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorsHolder;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.lootModifier.LootModifier;
import com.chen1335.legendaryRelics.compat.jei.network.TransferItem;
import com.chen1335.legendaryRelics.mixins.legendary_relics.CurioAttributeModifierEventInvoker;
import com.chen1335.legendaryRelics.network.*;
import com.chen1335.legendaryRelics.registers.attachmentDatas.LREntityData;
import com.chen1335.legendaryRelics.registers.attachmentDatas.LRProjectileData;
import com.chen1335.legendaryRelics.registers.dataComponentTypes.BowUsingArrow;
import com.chen1335.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonArmor;
import com.chen1335.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonHelmet;
import com.chen1335.legendaryRelics.registers.items.armor.blackDragonSet.BlackDragonLeggings;
import com.chen1335.legendaryRelics.registers.items.misc.AncientFragment;
import com.chen1335.legendaryRelics.registers.items.misc.DarkGoldForgingTool;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.shieldSystem.events.RegisterShieldPriorityEvent;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotAttribute;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;
import vazkii.patchouli.common.item.ItemModBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventHandler {
    @EventBusSubscriber(modid = LegendaryRelics.MODID)
    public static class Game {

        @SubscribeEvent
        public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (ModList.get().isLoaded("patchouli")) {
                LREntityData entityData = event.getEntity().getData(LRAttachmentTypes.ENTITY_DATA);
                if (!entityData.hasGiveBook) {
                    event.getEntity().addItem(ItemModBook.forBook(LegendaryRelics.id("legendary_relics_book")));
                    entityData.hasGiveBook = true;
                }
                if (!entityData.hasGiveCharmOfFreshStart) {
                    event.getEntity().addItem(LRItems.CHARM_OF_FRESH_START.toStack());
                    entityData.hasGiveCharmOfFreshStart = true;
                }
            }
            if (!event.getEntity().level().isClientSide) {
                PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new LootConfigPack(LootModifier.LOOT_ENTRIES.values().stream().toList()));

                CalculatorsHolder.getCalculators().forEach((locateInfo, finalCalculator) -> {
                    if (finalCalculator.changed()) {
                        PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new UpdateCalculatorPack(locateInfo, finalCalculator));
                    }
                });


            }
        }

        @SubscribeEvent
        public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getEntity().level().isClientSide) {
                CalculatorsHolder.getCalculators().values().forEach(FinalCalculator::reset);
            }
        }

        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            event.getEntity().setData(LRAttachmentTypes.ENTITY_DATA, event.getOriginal().getData(LRAttachmentTypes.ENTITY_DATA));
        }

        @SubscribeEvent
        public static void setItemSetsEffect(SetItemSetsEffectEvent event) {
            event.sets(LRSetsEffects.BLACK_DRAGON_ARMOR.value(), EquipmentTypes.HUMANOID_ARMOR,
                    LRItems.BLACK_DRAGON_HELMET.asItem(),
                    LRItems.BLACK_DRAGON_CHEST_PLATE.asItem(),
                    LRItems.BLACK_DRAGON_LEGGINGS.asItem(),
                    LRItems.BLACK_DRAGON_BOOTS.asItem()
            );
            event.sets(LRSetsEffects.INFERNO_ARMOR.value(), EquipmentTypes.HUMANOID_ARMOR,
                    LRItems.INFERNO_HELMET.asItem(),
                    LRItems.INFERNO_CHEST_PLATE.asItem(),
                    LRItems.INFERNO_LEGGINGS.asItem(),
                    LRItems.INFERNO_BOOTS.asItem()
            );
            event.sets(LRSetsEffects.TWISTED_ARMOR.value(), EquipmentTypes.HUMANOID_ARMOR,
                    LRItems.TWISTED_HELMET.asItem(),
                    LRItems.TWISTED_CHEST_PLATE.asItem(),
                    LRItems.TWISTED_LEGGINGS.asItem(),
                    LRItems.TWISTED_BOOTS.asItem()
            );
        }

        @SubscribeEvent
        public static void heal(LivingHealEvent event) {
            CalculatorArg arg = CalculatorArg.simpleArg(event.getEntity());
            LRItems.BLACK_DRAGON_LEGGINGS.get().runIfEquippedThis(event.getEntity(), arg, (itemStack, arg1) -> event.setAmount(event.getAmount() * (1 + BlackDragonLeggings.HEAL_INCREASE.getValue(arg1))));
        }

        @SubscribeEvent
        public static void MobEffectEvent$Added(MobEffectEvent.Added event) {
            ItemStack headArmor = event.getEntity().getItemBySlot(EquipmentSlot.HEAD);
            if (headArmor.getItem() == LRItems.BLACK_DRAGON_HELMET.get()) {
                CalculatorArg arg = CalculatorArg.emptyArg();
                CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, headArmor);
                MobEffectInstance effectInstance = event.getEffectInstance();
                if (effectInstance != null && effectInstance.duration != -1) {
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
        public static void LivingIncomingDamageEventLowest(LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();
            CalculatorArg args = CalculatorArg.simpleArg(entity);
            for (ItemStack armorSlot : entity.getArmorSlots()) {
                if (armorSlot.getItem() instanceof BlackDragonArmor blackDragonArmor) {
                    CalculatorArg args1 = args.copy();
                    CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args1, armorSlot);
                    blackDragonArmor.handleDamageReduce(event, args1, armorSlot);
                }
            }

            if (event.getSource().getDirectEntity() instanceof Projectile projectile) {
                LRProjectileData data = LRUtil.getProjectileData(projectile);
                if (data.damageMul != 1) {
                    event.setAmount(event.getAmount() * data.damageMul);
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void LivingIncomingDamageEventHighest(LivingIncomingDamageEvent event) {

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
                Holder<Attribute> holder = modifier.attribute();
                if (holder.isBound()) {
                    boolean isNeutral = false;
                    Attribute attribute = holder.value();
                    if (attribute.sentiment == Attribute.Sentiment.NEUTRAL) {
                        isNeutral = true;
                    } else if (attribute.sentiment == Attribute.Sentiment.POSITIVE && modifier.modifier().amount() < 0) {
                        i = 0;
                    } else if (attribute.sentiment == Attribute.Sentiment.NEGATIVE && modifier.modifier().amount() > 0) {
                        i = 0;
                    }

                    if (!isNeutral && i != 0) {
                        event.addModifier(modifier.attribute(), new AttributeModifier(LegendaryRelics.id("dark_gold_improve_" + modifier.slot().getSerializedName()), i * modifier.modifier().amount(), modifier.modifier().operation()), modifier.slot());
                    }
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
                Attribute value = attributeHolder.value();
                AttributeModifier modifier = entry.getValue();
                float j = i;
                if (value.sentiment == Attribute.Sentiment.NEUTRAL) {
                    j = 0;
                } else if (value.sentiment == Attribute.Sentiment.POSITIVE && modifier.amount() < 0) {
                    j = 0;
                } else if (value.sentiment == Attribute.Sentiment.NEGATIVE && modifier.amount() > 0) {
                    j = 0;
                } else if (value instanceof SlotAttribute) {
                    j = 0;
                }
                if (j != 0) {
                    event.removeModifier(attributeHolder, modifier);
                    event.addModifier(attributeHolder, new AttributeModifier(modifier.id(), (1 + j) * modifier.amount(), modifier.operation()));
                }
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void CurioCanEquipEvent(CurioCanEquipEvent event) {
            CuriosApi.getCuriosInventory(event.getEntity()).ifPresent(iCuriosItemHandler -> {
                if (!iCuriosItemHandler.findCurios(event.getStack().getItem()).isEmpty() && event.getStack().is(LRTags.Items.CAN_ONLY_WEAR_ONE)) {
                    event.setEquipResult(TriState.FALSE);
                }
            });
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

        @SubscribeEvent
        public static void SetArrowInBow(LivingEntityUseItemEvent.Start event) {
            ItemStack item = event.getItem();
            if (item.getItem() instanceof IRenderArrowBow) {
                ItemStack projectile = event.getEntity().getProjectile(item);
                if (projectile.getItem() instanceof ArrowItem arrowItem) {
                    item.set(LRDataComponentTypes.BOW_USING_ARROW, new BowUsingArrow(arrowItem));
                }
            }
        }

        @SubscribeEvent
        public static void ClearArrowInBow(LivingEntityUseItemEvent.Stop event) {
            ItemStack item = event.getItem();
            if (item.getItem() instanceof IRenderArrowBow) {
                item.remove(LRDataComponentTypes.BOW_USING_ARROW);
            }
        }

        @SubscribeEvent
        public static void LivingEquipmentChangeEvent(LivingEquipmentChangeEvent event) {
            ItemStack from = event.getFrom();
            ItemStack eventTo = event.getTo();
            if (from.getItem() instanceof ILRItemExtension extension) {
                extension.onEquipmentChangeFrom(from,eventTo,event.getEntity());
            }
            if (eventTo.getItem() instanceof ILRItemExtension extension) {
                extension.onEquipmentChangeTo(from,eventTo,event.getEntity());
            }
        }
    }

    @EventBusSubscriber(modid = LegendaryRelics.MODID)
    public static class Mod {
        @SubscribeEvent
        public static void RegisterPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(EffectCooldownPack.TYPE, EffectCooldownPack.STREAM_CODEC, EffectCooldownPack::handler);
            registrar.playToClient(SetsInfoPack.TYPE, SetsInfoPack.STREAM_CODEC, SetsInfoPack::handler);
            registrar.playToClient(PlayClientParticlePack.TYPE, PlayClientParticlePack.STREAM_CODEC, PlayClientParticlePack::handler);

            registrar.playBidirectional(LootConfigPack.TYPE, LootConfigPack.STREAM_CODEC, LootConfigPack::handler);

            registrar.playBidirectional(UpdateCalculatorPack.TYPE, UpdateCalculatorPack.STREAM_CODEC, UpdateCalculatorPack::handler);

            PayloadRegistrar optional = registrar.optional();
            if (ModList.get().isLoaded("jei")) {
                optional.playToServer(TransferItem.TYPE, TransferItem.STREAM_CODEC, TransferItem::handler);
            }
        }
    }
}
