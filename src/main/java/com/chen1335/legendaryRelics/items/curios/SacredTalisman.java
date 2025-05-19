package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurioHelper;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class SacredTalisman extends Item implements ICurioItem, LRCurioHelper {
    public SacredTalisman() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static FinalCalculator SHIELD_AMOUNT = FinalCalculator.of(
            Add.of(
                    Constant.of(4),
                    Mul.of(
                            Constant.of(0.8F),
                            EntityAttributeValue.of(Attributes.MAX_HEALTH)
                    )
            )
    );

    public static FinalCalculator MAX_HEALTH_PERCENTAGE = FinalCalculator.of(Constant.of(0.25F));

    public static FinalCalculator SHIELD_LAST_TIME = FinalCalculator.of(Constant.of(5));

    public static FinalCalculator UNDEAD_REDUCE = FinalCalculator.of(Constant.of(0.1F));

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        Level level = context.level();
        CalculatorArg args = new CalculatorArg();

        tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill.1", UNDEAD_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        tooltipComponents.add(Component.empty());

        if (level != null && level.isClientSide()) {
            CalculatorArg.ArgType.THIS_ENTITY.putArg(args, LRClient.getClientPlayer());
            tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill",
                    MAX_HEALTH_PERCENTAGE.toPercentageComponent(tooltipFlag.hasShiftDown(), args),
                    SHIELD_AMOUNT.toComponent(tooltipFlag.hasShiftDown(), args),
                    SHIELD_LAST_TIME.toComponent(tooltipFlag.hasShiftDown(), args)
            ).withColor(0xaeaeae));
        } else {
            tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill",
                    MAX_HEALTH_PERCENTAGE.toRawComponent(),
                    SHIELD_AMOUNT.toRawComponent(),
                    SHIELD_LAST_TIME.toRawComponent()
            ).withColor(0xaeaeae));
        }
        tooltipComponents.add(Component.translatable("item.legendary_relics.sacred_talisman.skill.desc").withColor(5592405));
        tooltipComponents.add(Component.translatable("legendary_relics.cooldown", 120).withColor(5592405));
    }
}
