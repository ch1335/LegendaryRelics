package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.EquipmentEffectLevelArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class FireDamageReduce extends LRCurioEffectBase {

    public FireDamageReduce(EffectType<?> effectType, int level) {
        super(effectType, level);
    }


    public FireDamageReduce(int level) {
        this(LREquipmentEffectTypes.FIRE_DAMAGE_REDUCE.value(), level);
    }

    public static FinalCalculator FIRE_DAMAGE_REDUCE = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(level -> 0.2F + level * 0.15F)
    ));

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.lava_ring.desc.1", FIRE_DAMAGE_REDUCE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleLivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        Optional<Pair<ItemStack, FireDamageReduce>> pairOptional = EquipmentEffectAPI.findBestEffect(event.getEntity(), LREquipmentEffectTypes.FIRE_DAMAGE_REDUCE.value());
        pairOptional.ifPresent(pair -> {
            if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
                event.setAmount(event.getAmount() * (1 - FIRE_DAMAGE_REDUCE.getValue(CalculatorArg.simpleArg(event.getEntity(), pair.getFirst(), pair.getSecond()))));
            }
        });
    }
}
