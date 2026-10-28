package com.chen1335.equipmentEffectLib.client;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;

import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT, modid = LegendaryRelics.MODID)
public class ClientEventHandler {
    @SubscribeEvent
    public static void AddAttributeTooltipsEvent(AddAttributeTooltipsEvent event) {
        ItemStack stack = event.getStack();
        Map<EffectType<?>, BaseEffect> effects = EquipmentEffectAPI.getEffects(stack);
        effects.values().forEach(baseEffect -> baseEffect.addAttributeTooltips(event.getStack(), event::addTooltipLines, event.getContext()));
    }
}
