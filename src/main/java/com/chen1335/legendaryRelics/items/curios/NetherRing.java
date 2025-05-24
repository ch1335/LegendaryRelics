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
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NetherRing extends LRCuriosBase {
    public NetherRing() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static FinalCalculator DAMAGE_INCREASE = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.1F),
            Constant.of(0.15F)
    ));

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.nether_ring.desc.1", DAMAGE_INCREASE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

    @Override
    public void onAttack(LivingIncomingDamageEvent event, CalculatorArg newArgs, ItemStack itemStack, LivingEntity attacker) {
        if (event.getEntity().isOnFire()) {
            event.setAmount(event.getAmount() * (1 + DAMAGE_INCREASE.getValue(newArgs)));
        }
    }
}
