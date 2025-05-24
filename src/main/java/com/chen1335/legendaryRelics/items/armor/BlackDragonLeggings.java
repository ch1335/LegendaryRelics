package com.chen1335.legendaryRelics.items.armor;

import com.chen1335.legendaryRelics.API.objects.LRArmorMaterials;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.Constant;
import com.chen1335.legendaryRelics.common.calculator.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackDragonLeggings extends BlackDragonArmor {
    public BlackDragonLeggings() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.LEGGINGS, new Properties().rarity(Rarity.EPIC));
    }

    public static FinalCalculator PROJECTILE_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.15F)
            )
    );

    public static FinalCalculator HEAL_INCREASE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.1F),
                    Constant.of(0.20F)
            )
    );



    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg arg = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_leggings.desc.1", PROJECTILE_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_leggings.desc.2", HEAL_INCREASE.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void handleDamageReduce(LivingIncomingDamageEvent event, CalculatorArg arg, ItemStack armorSlot) {
        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) {
            event.setAmount(event.getAmount() * (1 - PROJECTILE_DAMAGE_REDUCE.getValue(arg)));
        }
    }
}
