package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.legendaryRelics.dataComponentTypes.CollectedMinerals;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class TheOreCollectorsRing extends LRCuriosBase {
    public TheOreCollectorsRing() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }


    public static Unit DEFAULT = DarkGoldUpdateArg.of(
            Constant.of(1, 0),
            Constant.of(2, 0)
    );

    public static FinalCalculator TOTAL = FinalCalculator.of(
            new Task(
                    DEFAULT,
                    Add.of(
                            DEFAULT,
                            Constant.of(1, 0)
                    )
            ), 0
    );

    public static FinalCalculator COLLECTED_MINERALS_REQUIRE = FinalCalculator.of(Constant.of(10, 0), 0);

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg arg = CalculatorArg.emptyArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        tooltipComponents.add(Component.translatable("item.the_ore_collectors_ring.sacred_talisman.desc.1", TOTAL.toComponent(tooltipFlag.hasShiftDown(), arg), COLLECTED_MINERALS_REQUIRE.toComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.the_ore_collectors_ring.sacred_talisman.desc.2", Component.literal(String.valueOf(stack.getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS, CollectedMinerals.empty()).getCollectedOresAmount()))).withColor(0xaeaeae));

    }

    @Override
    public int getFortuneLevel(SlotContext slotContext, LootContext lootContext, ItemStack stack) {
        CalculatorArg arg = CalculatorArg.emptyArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        int addition = stack.getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS, CollectedMinerals.empty()).getCollectedOresAmount() >= COLLECTED_MINERALS_REQUIRE.getInt(arg) ? 1 : 0;
        return (int) (DEFAULT.getValue(arg) + addition);
    }

    public static class Task implements Unit {

        private final Unit a;
        private final Unit b;

        public Task(Unit a, Unit b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public float getValue(CalculatorArg calculatorArg) {
            return isFinished(calculatorArg) ? b.getValue(calculatorArg) : a.getValue(calculatorArg);
        }

        @Override
        public Component toComponent(CalculatorArg calculatorArg) {
            return isFinished(calculatorArg) ? b.toComponent(calculatorArg) : a.toComponent(calculatorArg);
        }

        private boolean isFinished(CalculatorArg calculatorArg) {
            @Nullable ItemStack itemStack = CalculatorArg.ArgType.THIS_ITEMS_STACK.getArg(calculatorArg);
            if (itemStack != null) {
                return itemStack.getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS, CollectedMinerals.empty()).getCollectedOresAmount() >= COLLECTED_MINERALS_REQUIRE.getInt(calculatorArg);
            }
            return false;
        }
    }
}
