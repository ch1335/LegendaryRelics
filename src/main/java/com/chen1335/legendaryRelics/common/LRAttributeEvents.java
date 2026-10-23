package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.API.objects.LRAttributes;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters.FrostKnight;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

/**
 * 属性挂载：自定义属性必须挂到实体类型上，否则 {@code LivingEntity#getAttribute} 返回 null，
 * 饰品就加不上 modifier（也就不会有任何同步）。
 */
@EventBusSubscriber(modid = LegendaryRelics.MODID)
public class LRAttributeEvents {

    // LOWEST：ApothicAttributes 在 NORMAL 也会注册这条属性，基础值由后注册者决定，所以必须排在它之后
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void EntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, LRAttributes.POWDER_SNOW_WALKING);
        // 冰川骷髅的投射物伤害基础值 1.5（神化默认 1.0）
        event.add(LREntityTypes.GLACIATE_SKELETON.get(), AttributesGetter.projectDamage(), 1.5D);
    }

    @SubscribeEvent
    public static void EntityAttributeCreation(EntityAttributeCreationEvent event) {
        // 与原版骷髅同属性
        event.put(LREntityTypes.GLACIATE_SKELETON.value(), AbstractSkeleton.createAttributes().build());
        event.put(LREntityTypes.FROST_KNIGHT.value(), FrostKnight.createAttributes().build());
    }
}
