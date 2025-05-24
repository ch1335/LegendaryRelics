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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackDragonBoots extends BlackDragonArmor {
    public BlackDragonBoots() {
        super(LRArmorMaterials.BLACK_DRAGON, Type.BOOTS, new Properties().rarity(Rarity.EPIC));
    }

    public static FinalCalculator ENVIRONMENT_DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.2F),
                    Constant.of(0.4F)
            )
    );


    public static FinalCalculator HEALTH_THRESHOLD = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.25F),
                    Constant.of(0.35F)
            )
    );

    public static FinalCalculator DAMAGE_REDUCE = FinalCalculator.of(
            DarkGoldUpdateArg.of(
                    Constant.of(0.10F),
                    Constant.of(0.15F)
            )
    );

    public static void handleLivingIncomingDamageEvent(LivingIncomingDamageEvent event, CalculatorArg newArgs, LivingEntity entity, ItemStack itemStack) {
        if (entity.getHealth() < entity.getMaxHealth() * HEALTH_THRESHOLD.getValue(newArgs)) {
            event.setAmount(event.getAmount() * (1 - DAMAGE_REDUCE.getValue(newArgs)));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg arg = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_boots.desc.1", ENVIRONMENT_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon_boots.desc.2", HEALTH_THRESHOLD.toPercentageComponent(tooltipFlag.hasShiftDown(), arg), DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), arg)).withColor(0xaeaeae));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public void handleDamageReduce(LivingIncomingDamageEvent event, CalculatorArg arg, ItemStack armorSlot) {
        if (event.getSource().is(Tags.DamageTypes.IS_ENVIRONMENT) || event.getSource().is(DamageTypeTags.IS_FALL)) {
            event.setAmount(event.getAmount() * (1 - ENVIRONMENT_DAMAGE_REDUCE.getValue(arg)));
        }
    }
}
