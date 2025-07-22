package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class Redemption extends LRCurioEffectBase {
    public Redemption(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public Redemption(int level) {
        this(LREquipmentEffectTypes.REDEMPTION.value(), level);
    }

    public static FinalCalculator SHIELD_AMOUNT = FinalCalculator.of(
            Add.of(
                    Constant.of(4),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.8F),
                                    Constant.of(1F)
                            ),
                            EntityAttributeValue.of(Attributes.MAX_HEALTH)
                    )
            )
    );

    public static FinalCalculator MAX_HEALTH_PERCENTAGE = FinalCalculator.of(Constant.of(0.25F));

    public static FinalCalculator SHIELD_LAST_TIME = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(5),
                    Constant.of(7.5F)
            )
    );

    public static FinalCalculator UNDEAD_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.2F)
            )
    );



    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill.1", UNDEAD_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill",
                MAX_HEALTH_PERCENTAGE.toPercentageComponent(tooltipFlag.hasShiftDown(), args),
                SHIELD_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                SHIELD_LAST_TIME.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill.desc").withColor(5592405));
        tooltipComponents.add(Component.translatable("legendary_relics.cooldown", 120).withColor(5592405));
    }


    @SubscribeEvent
    public static void onWearerBeDamage(LivingDamageEvent.Post event) {
        LivingEntity livingEntity = event.getEntity();
        if (EquipmentEffectCooldownManager.isNotInCooldown(livingEntity, LREquipmentEffectTypes.REDEMPTION.get())) {
            Optional<Pair<ItemStack, Redemption>> pair = EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.REDEMPTION.get());
            pair.ifPresent(itemStackRedemptionPair -> {
                CalculatorArg args = CalculatorArg.simpleArg(livingEntity, itemStackRedemptionPair.getFirst());
                if (livingEntity.getHealth() <= livingEntity.getMaxHealth() * MAX_HEALTH_PERCENTAGE.getValue(args)) {
                    ShieldAPI.addCommonTimeLimitedShield(livingEntity, SHIELD_AMOUNT.getValue(args), (int) (SHIELD_LAST_TIME.getValue(args) * 20));
                    if (livingEntity.isDeadOrDying()) {
                        livingEntity.setHealth(1);
                    }
                    EquipmentEffectCooldownManager.addCooldown(livingEntity, LREquipmentEffectTypes.REDEMPTION.get(), 120 * 20);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onWearerIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            LivingEntity livingEntity = event.getEntity();
            Optional<Pair<ItemStack, Redemption>> pair = EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.REDEMPTION.get());
            pair.ifPresent(itemStackRedemptionPair -> {
                if (attacker.getType().is(EntityTypeTags.UNDEAD)) {
                    CalculatorArg args = CalculatorArg.simpleArg(livingEntity, itemStackRedemptionPair.getFirst());
                    event.setAmount(event.getAmount() * (1 - UNDEAD_REDUCE.getValue(args)));
                }
            });
        }
    }
}
