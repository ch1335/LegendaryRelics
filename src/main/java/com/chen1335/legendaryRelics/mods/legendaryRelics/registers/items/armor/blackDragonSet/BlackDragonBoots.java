package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet;

import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation.Format;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackDragonBoots extends BlackDragonArmor {
    public BlackDragonBoots() {
        super(Type.BOOTS, new Properties().rarity(Rarity.EPIC));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(LREquipmentEffectTypes.FALL_IMMUNITY.value().create(1, EquipmentTypes.HUMANOID_ARMOR));
    }

    @Calculator
    public static FinalCalculator ENVIRONMENT_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.2F),
                    Constant.of(0.4F)
            )
    ).withFormat(Format.PERCENTAGE);

    @Calculator
    public static FinalCalculator HEALTH_THRESHOLD = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.25F),
                    Constant.of(0.35F)
            )
    ).withFormat(Format.PERCENTAGE);

    @Calculator
    public static FinalCalculator DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.10F),
                    Constant.of(0.15F)
            )
    ).withFormat(Format.PERCENTAGE);

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg arg = new CalculatorArg();
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(arg, tooltipFlag);
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_boots.desc.1", ENVIRONMENT_DAMAGE_REDUCE.toComponent(arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_boots.desc.2", HEALTH_THRESHOLD.toComponent(arg), DAMAGE_REDUCE.toComponent(arg)).withColor(0xaeaeae));
    }

    @Override
    protected void handleDamageReduce(LivingGetAttributeAdjustmentEvent event, CalculatorArg arg, ItemStack armorSlot, LivingEntity target, DamageSource source) {
        if (source.is(Tags.DamageTypes.IS_ENVIRONMENT) || source.is(DamageTypeTags.IS_FALL)) {
            event.getAdjustment(target, ArcanumAttributes.DAMAGE_TAKEN_MULTIPLIER).add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, -ENVIRONMENT_DAMAGE_REDUCE.getValue(arg));
        }
        if (target.getHealth() < target.getMaxHealth() * HEALTH_THRESHOLD.getValue(arg)) {
            event.getAdjustment(target, ArcanumAttributes.DAMAGE_TAKEN_MULTIPLIER).add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, -DAMAGE_REDUCE.getValue(arg));
        }
    }
}