package com.chen1335.legendaryRelics.equipmentEffects.armorEffect;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class BlackDragonChestPlateEffect extends LRArmorEffect {
    public BlackDragonChestPlateEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public BlackDragonChestPlateEffect(int level) {
        this(LREquipmentEffectTypes.BLACK_DRAGON_CHESTPLATE_EFFECT.value(), level);
    }

    public static FinalCalculator PHYSICAL_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            )
    );

    public static FinalCalculator DAMAGE_INCREASE = FinalCalculator.of(
            Add.of(
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(1F),
                                    Constant.of(1.5F)
                            ),
                            EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
                    ),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.1F),
                                    Constant.of(0.2F)
                            ),
                            EntityAttributeValue.of(Attributes.MAX_HEALTH)
                    )
            )
    );


    public static FinalCalculator SHIELD_AMOUNT = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            Constant.of(0.3F),
                            Constant.of(0.4F)
                    ),
                    EntityAttributeValue.of(Attributes.MAX_HEALTH)
            )
    );

    public static FinalCalculator COOL_DOWN = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(12F),
                    Constant.of(8F)
            )
    );

    public static FinalCalculator SHIELD_LAST_TIME = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(5F),
                    Constant.of(7F)
            )
    );

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        if (getRawEffectLevel() >1) {
            tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_chestplate.desc.1", PHYSICAL_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        }
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_chestplate.desc.2",
                DAMAGE_INCREASE.toComponent(tooltipFlag.hasShiftDown(), args),
                SHIELD_LAST_TIME.toComponent(tooltipFlag.hasShiftDown(), args),
                SHIELD_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                COOL_DOWN.toComponent(tooltipFlag.hasShiftDown(), args)
        ).withColor(0xaeaeae));

    }


    @SubscribeEvent
    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (EquipmentEffectCooldownManager.isNotInCooldown(attacker, LREquipmentEffectTypes.BLACK_DRAGON_CHESTPLATE_EFFECT.value())) {
                EquipmentEffectAPI.findBestEffect(attacker, LREquipmentEffectTypes.BLACK_DRAGON_CHESTPLATE_EFFECT.value()).ifPresent(pair -> {
                    CalculatorArg args = CalculatorArg.simpleArg(attacker, pair.getFirst(), pair.getSecond());
                    event.setAmount(event.getAmount() + DAMAGE_INCREASE.getValue(args));
                    ShieldAPI.addCommonDecayShield(attacker, SHIELD_AMOUNT.getValue(args), SHIELD_LAST_TIME.getInt(args) * 20);
                    EquipmentEffectCooldownManager.addCooldown(attacker, LREquipmentEffectTypes.BLACK_DRAGON_CHESTPLATE_EFFECT.value(), COOL_DOWN.getInt(args) * 20);
                });
            }
        }
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.BLACK_DRAGON_CHESTPLATE_EFFECT.value()).ifPresent(pair -> {
                if (event.getSource().is(Tags.DamageTypes.IS_PHYSICAL)) {
                    CalculatorArg args = CalculatorArg.simpleArg(livingEntity, pair.getFirst(), pair.getSecond());
                    event.setAmount(event.getAmount() * (1 - PHYSICAL_DAMAGE_REDUCE.getValue(args)));
                }
            });
        }
    }
}
