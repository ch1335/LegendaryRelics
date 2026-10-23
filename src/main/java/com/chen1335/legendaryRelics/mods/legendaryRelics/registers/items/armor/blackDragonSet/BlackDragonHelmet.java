package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.armor.blackDragonSet;

import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation.Format;

import com.chen1335.arcanumlib.modules.attribute.ArcanumAttributes;
import com.chen1335.arcanumlib.modules.attribute.AttributeAdjustment.AttributeAdjustment;
import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackDragonHelmet extends BlackDragonArmor {

    public BlackDragonHelmet() {
        super(Type.HELMET, new Properties().rarity(Rarity.EPIC));
    }

    @Calculator
    public static FinalCalculator MAGIC_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            )
    ).withFormat(Format.PERCENTAGE);
    @Calculator
    public static FinalCalculator GOOD_EFFECT_TIME_MULTIPLIER = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(2F),
                    Constant.of(2.5F)
            )
    );
    @Calculator
    public static FinalCalculator BAD_EFFECT_TIME_MULTIPLIER = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.75F),
                    Constant.of(0.5F)
            )
    );

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg arg = new CalculatorArg();
        CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(arg, tooltipFlag);
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_helmet.desc.1", ComponentHolders.Combat.magicDamage(), MAGIC_DAMAGE_REDUCE.toComponent(arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_helmet.desc.2", GOOD_EFFECT_TIME_MULTIPLIER.toComponent(arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_helmet.desc.3", BAD_EFFECT_TIME_MULTIPLIER.toComponent(arg)).withColor(0xaeaeae));

    }


    @Override
    protected void handleDamageReduce(LivingGetAttributeAdjustmentEvent event, CalculatorArg arg, ItemStack armorSlot, LivingEntity target, DamageSource source) {
        if (source.is(Tags.DamageTypes.IS_MAGIC)) {
            event.getAdjustment(target, ArcanumAttributes.DAMAGE_TAKEN_MULTIPLIER).add(AttributeAdjustment.Operation.ADD_MULTIPLIED_TOTAL, -MAGIC_DAMAGE_REDUCE.getValue(arg));
        }
    }
}