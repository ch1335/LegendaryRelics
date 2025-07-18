package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class HardenedEffect extends LRCurioEffectBase {
    public HardenedEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public HardenedEffect(int level) {
        this(LREquipmentEffectTypes.HARDENED_EFFECT.value(), level);
    }



    public static FinalCalculator ARMOR_AMOUNT = FinalCalculator.of(
            Add.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(2F),
                            Constant.of(4F)
                    ),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.50F),
                                    Constant.of(0.75F)
                            ),
                            EntityAttributeValue.of(Attributes.ARMOR)
                    )
            )
    );
    public static FinalCalculator TIME_KEEP = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(5),
            Constant.of(7.5F)
    ));
    public static FinalCalculator COOLDOWN = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(15),
            Constant.of(12.5F)
    ));

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.hardened_ring.skill",
                ARMOR_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                TIME_KEEP.toComponent(tooltipFlag.hasShiftDown(), args),
                COOLDOWN.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(0xaeaeae));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleLivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        Optional<Pair<ItemStack, HardenedEffect>> pairOptional = EquipmentEffectAPI.findBestEffect(event.getEntity(), LREquipmentEffectTypes.HARDENED_EFFECT.value());
        pairOptional.ifPresent(pair -> {
            LivingEntity living = event.getEntity();
            CalculatorArg calculatorArg = CalculatorArg.simpleArg(living, pair.getFirst(), pair.getSecond());
            if (!EquipmentEffectCooldownManager.isCooldown(living, LREquipmentEffectTypes.HARDENED_EFFECT.value())) {
                living.getData(LRAttachmentTypes.ENTITY_DATA).getTimeLimitedAttributeBonusManager()
                        .addAttributeModifier(
                                living,
                                Attributes.ARMOR,
                                new AttributeModifier(
                                        LegendaryRelics.id("hardened_ring_armor"),
                                        ARMOR_AMOUNT.getValue(calculatorArg),
                                        AttributeModifier.Operation.ADD_VALUE
                                ),
                                TIME_KEEP.getInt(calculatorArg) * 20
                        );
                EquipmentEffectCooldownManager.addCooldown(living, LREquipmentEffectTypes.HARDENED_EFFECT.value(), COOLDOWN.getInt(calculatorArg) * 20);
            }
        });
    }
}
