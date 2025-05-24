package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurio;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.common.AttributeModifierHolder;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.Constant;
import com.chen1335.legendaryRelics.common.calculator.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.utils.SimpleSchedule;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class PerseveranceNecklace extends CombineCurio {
    public PerseveranceNecklace() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    public static Multimap<Holder<Attribute>, AttributeModifierHolder> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ARMOR, new AttributeModifierHolder(0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            Attributes.MAX_HEALTH, new AttributeModifierHolder(0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );

    public static final Supplier<Set<LRCurio>> SUB_CURIOS = Suppliers.memoize(() -> {
        LinkedHashSet<LRCurio> hashSet = new LinkedHashSet<>();
        hashSet.add(LRItems.HEALING_TALISMAN.get());
        return hashSet;
    });

    public static FinalCalculator TIME = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(10F),
            Constant.of(8F)
    ));

    public static FinalCalculator DAMAGE_PERCENTAGE = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.15F),
            Constant.of(0.20F)
    ));

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.perseverance_necklace.desc.1", TIME.toComponent(tooltipFlag.hasShiftDown(), args), DAMAGE_PERCENTAGE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public Set<LRCurio> getCombinedCurios() {
        return SUB_CURIOS.get();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifierHolder> getAttributeModifierHolders() {
        return ATTRIBUTE_MODIFIERS;
    }

    @Override
    public void handleLivingDamageEventLowest(LivingDamageEvent.Post event, CalculatorArg calculatorArg, ItemStack itemStack) {
        int totalTime = TIME.getInt(calculatorArg) * 20;
        float totalHeal = DAMAGE_PERCENTAGE.getValue(calculatorArg) * event.getNewDamage();
        int runCount = totalTime / 10;
        float perHeal = totalHeal / runCount;
        SimpleSchedule.addSchedule(event.getEntity().level(), new SimpleSchedule.RepeatSchedule(runCount, 10, () -> {
            if (!event.getEntity().isRemoved()) {
                event.getEntity().heal(perHeal);
            }
        }));
    }
}
