package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.API.objects.LRAttributes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

/**
 * 属性挂载：自定义属性必须挂到实体类型上，否则 {@code LivingEntity#getAttribute} 返回 null，
 * 饰品就加不上 modifier（也就不会有任何同步）。
 */
@EventBusSubscriber(modid = LegendaryRelics.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LRAttributeEvents {

    @SubscribeEvent
    public static void EntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, LRAttributes.POWDER_SNOW_WALKING);
    }
}
