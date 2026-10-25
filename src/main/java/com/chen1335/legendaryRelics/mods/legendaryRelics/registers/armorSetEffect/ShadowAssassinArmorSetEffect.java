package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.armorSetEffect;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.arcanumlib.util.ComponentUtil;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntitySetsEffectData;
import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.equipmentEffectLib.utils.CombatUtil;
import com.chen1335.legendaryRelics.API.objects.LRSetsEffects;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation.Format;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.SpecialEffectStackArg;
import com.chen1335.legendaryRelics.common.calculator.special.TieredBonus;
import com.chen1335.legendaryRelics.effectInstances.ShadowAssassinEffectInstance;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.Lurk;
import com.chen1335.legendaryRelics.utils.LRColors;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import dev.shadowsoffire.apothic_attributes.api.ALObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 蓄势待发：停止攻击后逐渐叠加隐匿层数，攻击时一次性释放。
 */
public class ShadowAssassinArmorSetEffect extends SetEffect {

    @Calculator
    public static final FinalCalculator STACK_INTERVAL = FinalCalculator.of(
            TieredBonus.of(List.of(2F, 2F, 1.5F, 1F))
    ).withFormat(Format.NORMAL.unwrap().style(LRColors.Component.YELLOW.getStyle()).build());

    @Calculator
    public static final FinalCalculator CRIT_CHANCE_PER_STACK = FinalCalculator.of(
            TieredBonus.of(List.of(0.01F, 0.01F, 0.015F, 0.02F))
    ).withFormat(Format.PERCENTAGE.unwrap().style(LRColors.Component.BLUE.getStyle()).build());

    @Calculator
    public static final FinalCalculator CRIT_DAMAGE_PER_STACK = FinalCalculator.of(
            TieredBonus.of(List.of(0.03F, 0.03F, 0.04F, 0.05F))
    ).withFormat(Format.PERCENTAGE.unwrap().style(LRColors.Component.BLUE.getStyle()).build());

    @Calculator
    public static final FinalCalculator FULL_CRIT_CHANCE = FinalCalculator.of(
            TieredBonus.of(List.of(0.05F, 0.1F, 0.15F, 0.2F))
    ).withFormat(Format.PERCENTAGE.unwrap().style(LRColors.Component.BLUE.getStyle()).build());

    @Calculator
    public static final FinalCalculator FULL_CRIT_DAMAGE = FinalCalculator.of(
            TieredBonus.of(List.of(0.1F, 0.15F, 0.2F, 0.3F))
    ).withFormat(Format.PERCENTAGE.unwrap().style(LRColors.Component.BLUE.getStyle()).build());


    /**
     * 本次攻击的伤害修正：每层倍率 × 当前隐匿层数。
     */
    @Calculator
    public static final FinalCalculator ATTACK_DAMAGE_MULTIPLIER = FinalCalculator.of(
            Add.of(Constant.of(1),
                    Mul.of(
                            TieredBonus.of(List.of(0.32F, 0.32F, 0.4F, 0.5F)),
                            SpecialEffectStackArg.lurk()
                    )
            )
    ).withFormat(Format.PERCENTAGE.unwrap().style(LRColors.Component.RED.getStyle()).build());

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, List<Component> list) {
        CalculatorArg args = buildArg(player);
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(args, tooltipFlag);
        list.add(Component.translatable("set_effect_type.legendary_relics.tiered_bonus", Component.translatable("set_effect.legendary_relics.shadow_assassin.name").append("(%s/4)".formatted(getPiece(player))).withColor(16733695)).withColor(16755200));

        list.add(Component.translatable("set_effect.legendary_relics.shadow_assassin.desc.1",
                ComponentHolders.Combat.attack(),
                STACK_INTERVAL.toComponent(args),
                lurkComponent()
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.shadow_assassin.desc.2",
                lurkComponent(),
                ComponentHolders.Combat.attack(),
                ComponentUtil.Attributes.attribute(ALObjects.Attributes.CRIT_CHANCE, CRIT_CHANCE_PER_STACK.toComponent(args), "+"),
                ComponentUtil.Attributes.attribute(ALObjects.Attributes.CRIT_DAMAGE, CRIT_DAMAGE_PER_STACK.toComponent(args), "+")
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.shadow_assassin.desc.3",
                ComponentUtil.Attributes.attribute(ALObjects.Attributes.CRIT_CHANCE, FULL_CRIT_CHANCE.toComponent(args), "+"),
                ComponentUtil.Attributes.attribute(ALObjects.Attributes.CRIT_DAMAGE, FULL_CRIT_DAMAGE.toComponent(args), "+")
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.shadow_assassin.desc.4",
                ComponentHolders.Combat.attack(),
                ComponentUtil.Attributes.attribute(ArcanumAttributes.DAMAGE_MULTIPLIER, ATTACK_DAMAGE_MULTIPLIER.toComponent(args), "+")
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.armor_pieces_required", "2+"
        ).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static Component lurkComponent() {
        return Component.empty().append("").append(Component.translatable("legendary_relics.special_effect.lurk").withStyle(ChatFormatting.DARK_AQUA));
    }

    public static ShadowAssassinEffectInstance getEffectInstance(LivingEntity living) {
        return EntitySetsEffectData.getSetInstance(living, LRSetsEffects.SHADOW_ASSASSIN_ARMOR.value(), ShadowAssassinEffectInstance.class);
    }

    @Override
    public EffectInstance createInstance(int piece) {
        return new ShadowAssassinEffectInstance(piece);
    }

    /**
     * 每 tick 维护“停止攻击计时”和叠层计时。
     */
    public static void tickStacks(LivingEntity living, ShadowAssassinEffectInstance instance) {
        if (living.level().isClientSide || instance.getPiece() < 2) {
            return;
        }
        instance.ticksSinceAttack++;
        if (!instance.canGainStack()) {
            instance.stackTicks = 0;
            return;
        }
        CalculatorArg args = instance.buildArgs(living);
        int intervalTicks = Math.max(1, (int) (STACK_INTERVAL.getValue(args) * 20));
        if (++instance.stackTicks < intervalTicks) {
            return;
        }
        instance.stackTicks = 0;
        Lurk current = SpecialEffectAPI.getEffect(living, living.getUUID(), LRSpecialMobEffects.LURK.value()).orElse(null);
        if (current != null && current.getStack() >= Lurk.MAX_STACKS) {
            return;
        }
        Lurk lurk = new Lurk(LRSpecialMobEffects.LURK.value());
        lurk.setSourceEntity(living);
        SpecialEffectAPI.addEffectToEntity(living, lurk, Lurk::getFinal);
    }

    /**
     * 严格近战攻击会重置计时，并结算层数提供的暴击与伤害修正，随后清空层数。
     */
    public static void getAttributeAdjustment(LivingGetAttributeAdjustmentEvent event, DamageSource source,
                                              LivingEntity target, LivingEntity attacker) {
        if (!CombatUtil.isStrictAttack(source)) {
            return;
        }
        ShadowAssassinEffectInstance instance = getEffectInstance(attacker);
        if (instance == null) {
            return;
        }
        instance.resetAttackTimer();
        if (instance.getPiece() < 2) {
            return;
        }
        // 同一次攻击会多次查询属性，只在第一次查询时结算层数，后续命中共用同一份加成
        if (instance.lastAttackTick != attacker.tickCount) {
            instance.lastAttackTick = attacker.tickCount;
            instance.attackDamageBonus = 0;
            instance.attackCritChance = 0;
            instance.attackCritDamage = 0;
            Lurk lurk = SpecialEffectAPI.getEffect(attacker, attacker.getUUID(), LRSpecialMobEffects.LURK.value()).orElse(null);
            int stacks = lurk == null ? 0 : lurk.getStack();
            if (stacks > 0) {
                CalculatorArg args = instance.buildArgs(attacker);
                boolean full = stacks >= Lurk.MAX_STACKS;
                instance.attackCritChance = CRIT_CHANCE_PER_STACK.getValue(args) * stacks
                        + (full ? FULL_CRIT_CHANCE.getValue(args) : 0);
                instance.attackCritDamage = CRIT_DAMAGE_PER_STACK.getValue(args) * stacks
                        + (full ? FULL_CRIT_DAMAGE.getValue(args) : 0);
                instance.attackDamageBonus = ATTACK_DAMAGE_MULTIPLIER.getValue(args);
                lurk.consume();
            }
        }
        if (instance.attackCritChance > 0) {
            event.getAdjustment(attacker, ALObjects.Attributes.CRIT_CHANCE)
                    .add(AttributeAdjustment.Operation.ADD_VALUE, instance.attackCritChance);
        }
        if (instance.attackCritDamage > 0) {
            event.getAdjustment(attacker, ALObjects.Attributes.CRIT_DAMAGE)
                    .add(AttributeAdjustment.Operation.ADD_VALUE, instance.attackCritDamage);
        }
        if (instance.attackDamageBonus > 0) {
            event.getAdjustment(attacker, ArcanumAttributes.DAMAGE_MULTIPLIER)
                    .add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, instance.attackDamageBonus-1);
        }
    }
}
