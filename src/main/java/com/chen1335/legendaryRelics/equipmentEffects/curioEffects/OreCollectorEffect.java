package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.chen1335.legendaryRelics.dataComponentTypes.CollectedMinerals;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class OreCollectorEffect extends LRCurioEffectBase {
    public OreCollectorEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public OreCollectorEffect(int level) {
        this(LREquipmentEffectTypes.ORE_COLLECTOR_EFFECT.value(), level);
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
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.the_ore_collectors_ring.sacred_talisman.desc.1", TOTAL.toComponent(tooltipFlag.hasShiftDown(), args), COLLECTED_MINERALS_REQUIRE.toComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.the_ore_collectors_ring.sacred_talisman.desc.2", Component.literal(String.valueOf(itemStack.getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS, CollectedMinerals.empty()).getCollectedOresAmount()))).withColor(0xaeaeae));
    }

    @SubscribeEvent
    public static void countOres(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        EquipmentEffectAPI.findBestEffect(player, LREquipmentEffectTypes.ORE_COLLECTOR_EFFECT.value()).ifPresent(pair -> {
            CollectedMinerals.checkAndAdd(pair.getFirst(), event.getState().getBlock());
        });
    }

    @Override
    public boolean isBetterThan(LivingEntity entity, ItemStack thisItemStack, BaseEffect otherEffect, ItemStack otherStack) {
        return thisItemStack.getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS.value(), CollectedMinerals.empty()).ores().size() > otherStack.getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS.value(), CollectedMinerals.empty()).ores().size();
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
