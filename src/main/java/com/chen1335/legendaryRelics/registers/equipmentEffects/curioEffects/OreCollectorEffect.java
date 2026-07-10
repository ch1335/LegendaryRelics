package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.apothicAttributesExtension.API.objects.ModAttributes;
import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LRDataComponentTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.registers.dataComponentTypes.CollectedMinerals;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.List;

public class OreCollectorEffect extends LRCurioEffect {

    @Calculator
    public static final FinalCalculator DEFAULT = FinalCalculator.of(DarkGoldUpdateArg.of(
                    Constant.of(1, 0),
                    Constant.of(2, 0)
            )
    );

    @Calculator
    public static final FinalCalculator COLLECTED_MINERALS_REQUIRE = FinalCalculator.of(Constant.of(10, 0), 0);

    public OreCollectorEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.the_ore_collectors_ring.sacred_talisman.desc.1", DEFAULT.toComponent(tooltipFlag.hasShiftDown(), args), COLLECTED_MINERALS_REQUIRE.toComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("item.the_ore_collectors_ring.sacred_talisman.desc.2", Component.literal(String.valueOf(itemStack.getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS, CollectedMinerals.empty()).getCollectedOresAmount()))).withColor(0xaeaeae));
    }

    @SubscribeEvent
    public static void countOres(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        EquipmentEffectAPI.findBestEffect(player, LREquipmentEffectTypes.ORE_COLLECTOR_EFFECT.value()).ifPresent(pair -> {
            CollectedMinerals.checkAndAdd(pair.infoHolder().itemStack(), event.getState().getBlock());
        });
    }

    @Override
    public void modifyCurioAttribute(CurioAttributeModifierEvent event) {
        if (LegendaryRelics.isApothicAttributesExtensionLoaded()) {
            LivingEntity livingEntity = event.getSlotContext().entity();
            CalculatorArg args = CalculatorArg.simpleArg(livingEntity, event.getItemStack(), this);
            int addition = event.getItemStack().getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS, CollectedMinerals.empty()).getCollectedOresAmount() >= OreCollectorEffect.COLLECTED_MINERALS_REQUIRE.getInt(args) ? 1 : 0;
            event.addModifier(ModAttributes.MINING_FORTUNE, new AttributeModifier(LegendaryRelics.id("ore_collector_fortune"), (int) (OreCollectorEffect.DEFAULT.getValue(args) + addition), AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public boolean isBetterThan(LivingEntity entity, ItemStack thisItemStack, BaseEffect otherEffect, ItemStack otherStack) {
        return thisItemStack.getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS.value(), CollectedMinerals.empty()).ores().size() > otherStack.getOrDefault(LRDataComponentTypes.COLLECTED_MINERALS.value(), CollectedMinerals.empty()).ores().size();
    }
}
