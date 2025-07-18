package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;


import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.EquipmentEffectLevelArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.utils.AttributeModifyHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class AttributeBoostInNether extends LRCurioEffectBase {

    public static FinalCalculator ATTRIBUTE_BOOST = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(level -> 0.025F * level)
    ));

    @Override
    public int getDarkGoldLevelAdd() {

        return getRawEffectLevel();
    }

    public AttributeBoostInNether(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public AttributeBoostInNether(int level) {
        this(LREquipmentEffectTypes.ATTRIBUTE_BOOST_IN_NETHER.value(), level);
    }


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.nether_talisman.desc.1", ATTRIBUTE_BOOST.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

    private void addAttribute(LivingEntity living, ItemStack itemStack) {
        CalculatorArg arg = CalculatorArg.simpleArg(living, itemStack, this);
        AttributeModifyHelper.addAllPositive(living, getModifierId(), ATTRIBUTE_BOOST.getValue(arg), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    }

    private void removeAttribute(LivingEntity living, ItemStack itemStack) {
        AttributeModifyHelper.removeAllPositive(living, getModifierId());
    }

    @Override
    public void onDeActive(LivingEntity entity, ItemStack itemStack) {
        if (entity.level().dimension().equals(ServerLevel.NETHER)) {
            removeAttribute(entity, itemStack);
        }
    }

    @Override
    public void onActive(LivingEntity entity, ItemStack itemStack) {
        if (entity.level().dimension().equals(ServerLevel.NETHER)) {
            addAttribute(entity, itemStack);
        }
    }

    public ResourceLocation getModifierId() {
        return LegendaryRelics.id("nether_multiplier_" + this.hashCode());
    }

    @SubscribeEvent
    public static void HandleEntityTravelToDimensionEvent(EntityTravelToDimensionEvent event) {

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            Optional<Pair<ItemStack, AttributeBoostInNether>> pairOptional = EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.ATTRIBUTE_BOOST_IN_NETHER.value());
            pairOptional.ifPresent(pair -> {
                AttributeBoostInNether effect = pair.getSecond();
                if (event.getDimension().equals(ServerLevel.NETHER)) {
                    effect.addAttribute(livingEntity, pair.getFirst());
                } else {
                    effect.removeAttribute(livingEntity, pair.getFirst());
                }
            });

        }
    }
}
