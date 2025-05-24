package com.chen1335.legendaryRelics.armorSetEffect;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.legendaryRelics.items.armor.BlackDragonArmor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackDragonArmorSetEffect extends ArmorSetEffect {
    public static final ResourceLocation BLACK_DRAGON_ATTRIBUTE_MULTIPLIER = LegendaryRelics.id("black_dragon_attribute_multiplier");
    public static CustomArg<LivingEntity> BLACK_ARMOR_COUNT_GETTER = CustomArg.of(CalculatorArg.ArgType.THIS_ENTITY, livingEntity -> {
        int i = 0;
        for (ItemStack armorSlot : livingEntity.getArmorSlots()) {
            if (armorSlot.getItem() instanceof BlackDragonArmor) {
                i++;
            }
        }
        return (float) i;
    }, Component.translatable("item.legendary_relics.arg_name.already_equipped_black_dragon_armor").withColor(16733695));


    public static FinalCalculator ATTRIBUTE_MULTIPLIER = FinalCalculator.of(
            Add.of(
                    Constant.of(0.02F,3),
                    Mul.of(
                            Constant.of(0.02F, 3),
                            BLACK_ARMOR_COUNT_GETTER
                    )
            )
    );

    public static FinalCalculator HEALTH_REGAIN = FinalCalculator.of(
            Add.of(
                    Constant.of(1),
                    Mul.of(
                            Constant.of(0.01F),
                            Mul.of(
                                    EntityAttributeValue.of(Attributes.MAX_HEALTH),
                                    BLACK_ARMOR_COUNT_GETTER
                            )
                    )
            )
    );

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        CalculatorArg args = new CalculatorArg();
        tooltipComponents.add(Component.translatable("item.legendary_relics.set_effect.desc.1", Component.translatable("item.legendary_relics.black_dragon_set_effect.name").withColor(16733695)).withColor(16755200));
        if (level != null && level.isClientSide()) {
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, LRClient.getClientPlayer());
            tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon.desc.1",
                    ATTRIBUTE_MULTIPLIER.toPercentageComponent(tooltipFlag.hasShiftDown(), args)
            ).withColor(16733695));

            tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon.desc.2",
                    HEALTH_REGAIN.toComponent(tooltipFlag.hasShiftDown(), args)
            ).withColor(16733695));
        } else {
            tooltipComponents.add(Component.translatable("item.legendary_relics.black_dragon.desc.1",
                    ATTRIBUTE_MULTIPLIER.toRawComponent()
            ).withColor(16733695));
        }
    }

}
