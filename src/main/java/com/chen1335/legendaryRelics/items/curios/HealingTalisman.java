package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.Constant;
import com.chen1335.legendaryRelics.common.calculator.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HealingTalisman extends LRCuriosBase {
    public HealingTalisman() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static FinalCalculator HEAL_INCREASE = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.05F),
            Constant.of(0.1F)
    ));

    public static FinalCalculator HEAL_PER_5S = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(1F),
            Constant.of(1.5F)
    ));

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg arg = CalculatorArg.emptyArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.healing_talisman.desc.1", HEAL_INCREASE.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.healing_talisman.desc.2", HEAL_PER_5S.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
    }

    @Override
    public void handleLivingHealEvent(LivingHealEvent event, CalculatorArg calculatorArg, ItemStack itemStack) {
        event.setAmount(event.getAmount() * (1 + HEAL_INCREASE.getValue(calculatorArg)));
    }

    @Override
    public void handleTickEvent(EntityTickEvent.Pre event, CalculatorArg calculatorArg, ItemStack itemStack, LivingEntity living) {
        if (living.level().getGameTime() % 10 == 0) {
            living.heal(HEAL_PER_5S.getValue(calculatorArg) / 10);
        }
    }
}
