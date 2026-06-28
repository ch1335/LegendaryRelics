package com.chen1335.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.common.attributeFix.AttributeFixer;
import com.chen1335.legendaryRelics.common.attributeFix.fixTypes.MulFix;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

public class InfernoScorch extends TimeLimitEffect {
    private float damage;
    private float explosionDamage;

    public InfernoScorch(MobEffectType<?> effectType) {
        super(effectType);
    }

    public InfernoScorch(float damagePerSecond,float explosionDamage) {
        super(LRSpecialMobEffects.INFERNO_SCORCH.value());
        this.damage = damagePerSecond;
        this.explosionDamage = explosionDamage;
        initTime(120);
    }

    @Override
    public void tick(LivingEntity livingEntity) {
        super.tick(livingEntity);
        Level level = livingEntity.level();
        if (!level.isClientSide) {
            if (level.getGameTime() % 20 == 0) {
                @Nullable Entity sourceEntity = getSourceEntity(livingEntity.level());
                if (sourceEntity instanceof LivingEntity sourceLiving) {
                    AttributeFixer.runWhileFix(livingEntity, Attributes.ARMOR, new MulFix(0.5F), () -> {
                        livingEntity.hurt(level.damageSources().source(LRDamageTypes.INFERNO_SCORCH, sourceLiving), damage);
                    });
                }
            }
        } else {
            RandomSource random = livingEntity.getRandom();
            AABB boundingBox = livingEntity.getBoundingBox();
            double x = Mth.lerp(random.nextFloat(), boundingBox.minX, boundingBox.maxX);
            double y = Mth.lerp(random.nextFloat(), boundingBox.minY, boundingBox.maxY);
            double z = Mth.lerp(random.nextFloat(), boundingBox.minZ, boundingBox.maxZ);
            level.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);

            PartEntity<?>[] parts = livingEntity.getParts();
            if (livingEntity.isMultipartEntity()) {
                for (PartEntity<?> part : parts) {
                    AABB boundingBox1 = part.getBoundingBox();
                    double x1 = Mth.lerp(random.nextFloat(), boundingBox1.minX, boundingBox1.maxX);
                    double y1 = Mth.lerp(random.nextFloat(), boundingBox1.minY, boundingBox1.maxY);
                    double z1 = Mth.lerp(random.nextFloat(), boundingBox1.minZ, boundingBox1.maxZ);
                    level.addParticle(ParticleTypes.FLAME, x1, y1, z1, 0, 0, 0);
                }
            }

        }
    }

    public InfernoScorch getFinal(InfernoScorch infernoScorch) {
        return this;
    }

    public float getExplosionDamage() {
        return explosionDamage;
    }

    public float getDamage() {
        return damage;
    }
}
