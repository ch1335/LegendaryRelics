package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters.FrostKnight;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters.GlaciateSkeleton;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.misc.IceThorn;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.FlyingKnife;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.FlyingReaper;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.Icicle;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.OrbitingFireChakram;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.ThrownFrostShard;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.TreatmentBall;
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

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownFrostShard>> THROWN_FROST_SHARD = ENTITY_TYPES.register("thrown_frost_shard", () -> EntityType.Builder.<ThrownFrostShard>of(ThrownFrostShard::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20).build("thrown_frost_shard"));

    public static final DeferredHolder<EntityType<?>, EntityType<Icicle>> ICICLE = ENTITY_TYPES.register("icicle", () -> EntityType.Builder.<Icicle>of(Icicle::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(1).build("icicle"));

    /** 冰刺：从地下钻出、刺伤上方目标的地面陷阱。 */
    public static final DeferredHolder<EntityType<?>, EntityType<IceThorn>> ICE_THORN = ENTITY_TYPES.register("ice_thorn", () -> EntityType.Builder.<IceThorn>of(IceThorn::new, MobCategory.MISC).sized(IceThorn.WIDTH, IceThorn.HEIGHT).clientTrackingRange(8).updateInterval(1).build("ice_thorn"));

    /** 烈焰飞轮的环绕实体：位置每 tick 变，所以同步间隔要小。 */
    public static final DeferredHolder<EntityType<?>, EntityType<OrbitingFireChakram>> ORBITING_FIRE_CHAKRAM = ENTITY_TYPES.register("orbiting_fire_chakram", () -> EntityType.Builder.<OrbitingFireChakram>of(OrbitingFireChakram::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(64).updateInterval(1).build("orbiting_fire_chakram"));

    public static final DeferredHolder<EntityType<?>, EntityType<GlaciateSkeleton>> GLACIATE_SKELETON = ENTITY_TYPES.register("glaciate_skeleton", () -> EntityType.Builder.<GlaciateSkeleton>of(GlaciateSkeleton::new, MobCategory.MONSTER).sized(0.6F, 1.99F).eyeHeight(1.74F).clientTrackingRange(8).build("glacite_skeleton"));

    public static final DeferredHolder<EntityType<?>, EntityType<FrostKnight>> FROST_KNIGHT = ENTITY_TYPES.register("frost_knight", () -> EntityType.Builder.<FrostKnight>of(FrostKnight::new, MobCategory.MONSTER).sized(0.7F, 2.4F).eyeHeight(2.1F).clientTrackingRange(10).fireImmune().build("frost_knight"));


}
