package com.chen1335.legendaryRelics.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
public class DarkSteelClawEffect extends LRCurioEffectBase {
    public DarkSteelClawEffect(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    public DarkSteelClawEffect(int level) {
        this(LREquipmentEffectTypes.DARK_STEEL_CLAW_EFFECT.value(), level);
    }

    public static FinalCalculator DAMAGE_ADD = FinalCalculator.of(
            Mul.of(
                    DarkGoldUpdateArg.of(
                            EquipmentEffectLevelArg.of(level -> 0.1F + level * 0.05F)
                    ),
                    EntityAttributeValue.of(Attributes.ARMOR)
            )
    );

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg args = CalculatorArg.simpleArg(player, itemStack, this);
        tooltipComponents.add(Component.translatable("item.legendary_relics.dark_steel_claw.skill", DAMAGE_ADD.toComponent(tooltipFlag.hasShiftDown(), args)).withColor(0xaeaeae));
        tooltipComponents.add(Component.translatable("legendary_relics.items.skill.cannot_be_tacked").withColor(5592405));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void handleAttack(LivingIncomingDamageEvent event) {
        if (event.getSource().is(DamageTypes.PLAYER_ATTACK) && event.getSource().getEntity() instanceof LivingEntity livingEntity) {
            Optional<Pair<ItemStack, DarkSteelClawEffect>> pairOptional = EquipmentEffectAPI.findBestEffect(livingEntity, LREquipmentEffectTypes.DARK_STEEL_CLAW_EFFECT.value());
            pairOptional.ifPresent(pair -> {
                event.setAmount(event.getAmount() + DAMAGE_ADD.getValue(CalculatorArg.simpleArg(livingEntity, pair.getFirst(), pair.getSecond())));
            });
        }
    }
}
