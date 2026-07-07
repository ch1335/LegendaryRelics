package com.chen1335.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;

public class FlameImmunity extends LRCurioEffect {

    public FlameImmunity(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        tooltipComponents.add(Component.translatable("equipment_effect.legendary_relics.flame_immunity").withColor(0xaeaeae));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleLivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        EquipmentEffectAPI.findBestEffect(event.getEntity(), LREquipmentEffectTypes.FLAME_IMMUNITY.value()).ifPresent(pair -> {
            if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
                event.setCanceled(true);
            }
        });
    }
}
