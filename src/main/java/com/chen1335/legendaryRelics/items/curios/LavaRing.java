package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.Constant;
import com.chen1335.legendaryRelics.common.calculator.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LavaRing extends LRCuriosBase {
    public LavaRing() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static FinalCalculator FIRE_DAMAGE_REDUCE = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.35F),
            Constant.of(0.50F)
    ));

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg arg = CalculatorArg.emptyArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.lava_ring.desc.1", FIRE_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));

    }

    @Override
    public void handleLivingIncomingDamageEventLowest(LivingIncomingDamageEvent event, CalculatorArg arg, ItemStack itemStack) {
        if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            event.setAmount(event.getAmount() * (1 - FIRE_DAMAGE_REDUCE.getValue(arg)));
        }
    }
}
