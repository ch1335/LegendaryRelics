package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.Constant;
import com.chen1335.legendaryRelics.common.calculator.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AgglomerationMalice extends LRCuriosBase {
    public AgglomerationMalice() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static FinalCalculator DAMAGE_MULTIPLIER = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(2),
            Constant.of(1.5F)
    ));

    public static FinalCalculator LOOTING_LEVEL_ADD = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(2),
            Constant.of(3F)
    ));

    public static int modifyLoot(Mob mob, LivingEntity livingAttacker, int lootingLevel) {
        CalculatorArg arg = new CalculatorArg();
        ItemStack itemStack = LRItems.AGGLOMERATION_MALICE.value().getEquippedThis(livingAttacker);
        if (mob.getSpawnType() == MobSpawnType.SPAWNER && itemStack != null) {
            CalculatorArg.ArgType.THIS_ENTITY.putArg(arg, livingAttacker);
            CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(arg, itemStack);
            int lootingLevelAddition = (int) LOOTING_LEVEL_ADD.getValue(arg);
            return lootingLevel + lootingLevelAddition;
        }
        return lootingLevel;
    }

    public static void handleLivingIncomingDamageEvent(LivingIncomingDamageEvent event, CalculatorArg args, LivingEntity entity, ItemStack itemStack) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (attacker instanceof Mob) {
                if (((Mob) attacker).getSpawnType() == MobSpawnType.SPAWNER && LRItems.AGGLOMERATION_MALICE.value().isEquippedThis(event.getEntity())) {
                    event.setAmount(event.getAmount() * AgglomerationMalice.DAMAGE_MULTIPLIER.getValue(args));
                }
            }
        }
    }

    @Override
    public void handleLivingIncomingDamageEventLowest(LivingIncomingDamageEvent event, CalculatorArg newArgs, ItemStack itemStack) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (attacker instanceof Mob) {
                if (((Mob) attacker).getSpawnType() == MobSpawnType.SPAWNER && LRItems.AGGLOMERATION_MALICE.value().isEquippedThis(event.getEntity())) {
                    event.setAmount(event.getAmount() * AgglomerationMalice.DAMAGE_MULTIPLIER.getValue(newArgs));
                }
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CalculatorArg args = new CalculatorArg();
        CalculatorArg.ArgType.THIS_ITEMS_STACK.putArg(args, stack);
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.desc").withColor(5592405));
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.bad_effect", DAMAGE_MULTIPLIER.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(16733525));
        tooltipComponents.add(Component.translatable("item.legendary_relics.agglomeration_malice.good_effect", LOOTING_LEVEL_ADD.toComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

}
