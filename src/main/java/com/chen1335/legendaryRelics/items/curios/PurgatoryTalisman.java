package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurio;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.common.AttributeModifierHolder;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.Constant;
import com.chen1335.legendaryRelics.common.calculator.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.utils.AttributeModifyHelper;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;


public class PurgatoryTalisman extends CombineCurio {
    public PurgatoryTalisman() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1));
    }

    public static final Supplier<Set<LRCurio>> SUB_CURIOS = Suppliers.memoize(() -> Set.of(
            LRItems.LAVA_RING.value(),
            LRItems.NETHER_RING.value()
    ));

    public static FinalCalculator ATTRIBUTE_BOOST = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(0.05F),
            Constant.of(0.1F)
    ));


    public static Multimap<Holder<Attribute>, AttributeModifierHolder> ATTRIBUTE_MODIFIERS = ImmutableMultimap.of(
            Attributes.ATTACK_SPEED, new AttributeModifierHolder(0.1F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            Attributes.ATTACK_DAMAGE, new AttributeModifierHolder(0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
            Attributes.BURNING_TIME, new AttributeModifierHolder(-0.8F, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)

    );

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.nether_talisman.desc.1", ATTRIBUTE_BOOST.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void handleEntityTravelToDimensionEvent(EntityTravelToDimensionEvent event, CalculatorArg calculatorArg, ItemStack itemStack, LivingEntity livingEntity) {
        updateAttributeState(livingEntity, calculatorArg, event.getDimension() == Level.NETHER);
    }

    @Override
    public void handleCurioChangeEvent(CurioChangeEvent event, CalculatorArg calculatorArg, ItemStack itemStack, LivingEntity entity) {
        updateAttributeState(entity, calculatorArg, entity.level().dimension() == Level.NETHER);
    }

    private void updateAttributeState(LivingEntity livingEntity, CalculatorArg arg, boolean active) {
        if (isEquippedThis(livingEntity)) {
            if (active) {
                AttributeModifyHelper.addAllPositive(livingEntity, NetherTalisman.NETHER_TALISMAN_ATTRIBUTE_MULTIPLIER, ATTRIBUTE_BOOST.getValue(arg), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            } else {
                AttributeModifyHelper.removeAllPositive(livingEntity, NetherTalisman.NETHER_TALISMAN_ATTRIBUTE_MULTIPLIER);
            }
        } else {
            AttributeModifyHelper.removeAllPositive(livingEntity, NetherTalisman.NETHER_TALISMAN_ATTRIBUTE_MULTIPLIER);
        }
    }

    @Override
    public Set<LRCurio> getCombinedCurios() {
        return SUB_CURIOS.get();
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifierHolder> getAttributeModifierHolders() {
        return ATTRIBUTE_MODIFIERS;
    }
}
