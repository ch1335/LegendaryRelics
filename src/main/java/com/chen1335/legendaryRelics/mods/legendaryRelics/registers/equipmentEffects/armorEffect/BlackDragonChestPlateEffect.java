package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.armorEffect;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.shieldSystem.API.shieldAPI.ShieldAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public class BlackDragonChestPlateEffect extends LRArmorEffect {

    @Calculator
    public static final FinalCalculator PHYSICAL_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            )
    );

    @Calculator
    public static final FinalCalculator DAMAGE_INCREASE = FinalCalculator.of(
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

    @Calculator
    public static final FinalCalculator SHIELD_AMOUNT = FinalCalculator.of(
            Add.of(Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.2F),
                                    Constant.of(0.3F)
                            ),
                            EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
                    ),
                    Mul.of(
                            DarkGoldUpdateArg.of(
                                    Constant.of(0.3F),
                                    Constant.of(0.4F)
                            ),
                            EntityAttributeValue.of(Attributes.MAX_HEALTH)
                    )
            )
    );

    @Calculator
    public static final FinalCalculator COOL_DOWN = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(12F),
                    Constant.of(8F)
            )
    );

    @Calculator
    public static final FinalCalculator SHIELD_LAST_TIME = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(5F),
                    Constant.of(7F)
            )
    );

    public BlackDragonChestPlateEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(args, tooltipFlag);
        if (getRawEffectLevel() > 1) {
            tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_chestplate.desc.1", ComponentHolders.Combat.physicalDamage(), PHYSICAL_DAMAGE_REDUCE.toComponent(args, Style.EMPTY, 100, "%s%%")).withColor(0xaeaeae));
        }
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("item.legendary_relics.black_dragon_chestplate.desc.2",
                DAMAGE_INCREASE.toComponent(args, Style.EMPTY),
                SHIELD_LAST_TIME.toComponent(args, Style.EMPTY),
                SHIELD_AMOUNT.toComponent(args, Style.EMPTY),
                COOL_DOWN.toComponent(args, Style.EMPTY)
        ).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));

    }

    public static void getAttributeAdjustment(LivingGetAttributeAdjustmentEvent event, DamageSource source, LivingEntity target, LivingEntity attacker) {
        if (source.is(Tags.DamageTypes.IS_PHYSICAL)) {
            LREquipmentEffectTypes.BLACK_DRAGON_CHESTPLATE_EFFECT.value().findBestEffect(target).ifPresent(pair -> {
                CalculatorArg args = CalculatorArg.simpleArg(target, pair.infoHolder().itemStack(), pair.infoHolder().effect());
                event.getAdjustment(target, ArcanumAttributes.DAMAGE_TAKEN_MULTIPLIER)
                        .add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, -PHYSICAL_DAMAGE_REDUCE.getValue(args));
            });
        }
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        EffectType<BlackDragonChestPlateEffect> effectType = LREquipmentEffectTypes.BLACK_DRAGON_CHESTPLATE_EFFECT.value();

        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (effectType.isNotInCooldown(attacker)) {
                effectType.findBestEffect(attacker).ifPresent(pair -> {
                    CalculatorArg args = CalculatorArg.simpleArg(attacker, pair.infoHolder().itemStack(), pair.infoHolder().effect());
                    event.setAmount(event.getAmount() + DAMAGE_INCREASE.getValue(args));
                    ShieldAPI.addCommonDecayShield(attacker, SHIELD_AMOUNT.getValue(args), SHIELD_LAST_TIME.getInt(args) * 20);
                    EquipmentEffectCooldownManager.addCooldown(attacker, effectType, COOL_DOWN.getInt(args) * 20);
                });
            }
        }


    }
}
