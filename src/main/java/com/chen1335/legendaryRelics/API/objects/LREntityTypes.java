package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.entities.TreatmentBall;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LREntityTypes {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, LegendaryRelics.MODID);

    public static DeferredHolder<EntityType<?>, EntityType<TreatmentBall>> TREATMENT_BALL = ENTITY_TYPES.register("treatment_ball", () -> EntityType.Builder.<TreatmentBall>of(TreatmentBall::new, MobCategory.MISC).sized(0.25F, 0.25F).updateInterval(1).build("treatment_ball"));
}
