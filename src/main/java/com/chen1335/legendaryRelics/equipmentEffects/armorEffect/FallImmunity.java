package com.chen1335.legendaryRelics.equipmentEffects.armorEffect;

import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class FallImmunity extends LRArmorEffect {
    public FallImmunity(int level) {
        super(LREquipmentEffectTypes.FALL_IMMUNITY.value(), level);
    }

    public FallImmunity(EffectType<?> effectType, int level) {
        super(effectType, level);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.fall_immunity").withColor(0xaeaeae));
    }

    @SubscribeEvent
    public static void immunityFallDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().is(DamageTypeTags.IS_FALL) && event.getEntity() instanceof LivingEntity livingEntity) {
            LREquipmentEffectTypes.FALL_IMMUNITY.value().findBestEffect(livingEntity).ifPresent(pair -> {
                event.setCanceled(true);
            });
        }
    }
}
