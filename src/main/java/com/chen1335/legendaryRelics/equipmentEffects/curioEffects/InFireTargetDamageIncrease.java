package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.damageController.API.DamageControllerAPI;
import com.chen1335.damageController.API.IDamageContainerGetter;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class InFireTargetDamageIncrease extends LRCurioEffectBase {
    public InFireTargetDamageIncrease(EffectType<InFireTargetDamageIncrease> effectType, int level) {
        super(effectType, level);
    }

    public InFireTargetDamageIncrease(int level) {
        this(LREquipmentEffectTypes.IN_FIRE_TARGET_DAMAGE_INCREASE.value(), level);
    }

    public static FinalCalculator DAMAGE_INCREASE = FinalCalculator.of(DarkGoldUpdateArg.of(
            EquipmentEffectLevelArg.of(level -> 0.05F + level * 0.05F)
    ));

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.nether_ring.desc.1", DAMAGE_INCREASE.toPercentageComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
    }

    @SubscribeEvent()
    public static void handleAttack(LivingIncomingDamageEvent event) {
        if (event.getEntity().isOnFire() && event.getSource().getEntity() instanceof LivingEntity attacker) {
            Optional<Pair<ItemStack, InFireTargetDamageIncrease>> pairOptional = EquipmentEffectAPI.findBestEffect(attacker, LREquipmentEffectTypes.IN_FIRE_TARGET_DAMAGE_INCREASE.value());
            pairOptional.ifPresent(pair -> {
                DamageControllerAPI.addMultipliedBase((IDamageContainerGetter) event, DAMAGE_INCREASE.getValue(CalculatorArg.simpleArg(attacker, pair.getFirst(), pair.getSecond())));
            });
        }
    }
}
