package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.FlyingKnife;
import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.FlyingReaper;
import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.TreatmentBall;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LREntityTypes {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, LegendaryRelics.MODID);

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }

    public static final DeferredHolder<EntityType<?>, EntityType<TreatmentBall>> TREATMENT_BALL = ENTITY_TYPES.register("treatment_ball", () -> EntityType.Builder.<TreatmentBall>of(TreatmentBall::new, MobCategory.MISC).sized(0.25F, 0.25F).updateInterval(1).build("treatment_ball"));

    public static final DeferredHolder<EntityType<?>, EntityType<FlyingReaper>> FLYING_REAPER = ENTITY_TYPES.register("flying_reaper", () -> EntityType.Builder.<FlyingReaper>of(FlyingReaper::new, MobCategory.MISC).sized(0.5F, 1.5F).updateInterval(1).build("flying_reaper"));

    public static final DeferredHolder<EntityType<?>, EntityType<FlyingKnife>> FLYING_KNIFE = ENTITY_TYPES.register("flying_knife", () -> EntityType.Builder.<FlyingKnife>of(FlyingKnife::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build("flying_knife"));


}
