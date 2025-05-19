package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.LRCurioHelper;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.Constant;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AgglomerationMalice extends Item implements LRCurioHelper {
    public AgglomerationMalice() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static FinalCalculator DAMAGE_MULTIPLIER = FinalCalculator.of(Constant.of(2F));

    public static FinalCalculator LOOTING_LEVEL_ADD = FinalCalculator.of(Constant.of(2));

    public static int modifyLoot(Mob mob, LivingEntity livingAttacker, int lootingLevel) {
        if (mob.getSpawnType() == MobSpawnType.SPAWNER && LRItems.AGGLOMERATION_MALICE.value().isEquippedThis(livingAttacker)) {
            int lootingLevelAddition = (int) LOOTING_LEVEL_ADD.getValue(CalculatorArg.emptyArg());
            return lootingLevel + lootingLevelAddition;
        }
        return lootingLevel;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg args = new CalculatorArg();
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.desc").withColor(5592405));
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.bad_effect", DAMAGE_MULTIPLIER.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(16733525));
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.good_effect", LOOTING_LEVEL_ADD.toComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

}
