package com.chen1335.legendaryRelics.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffect;
import com.chen1335.legendaryRelics.entities.TreatmentBall;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class Erosion extends TimeLimitEffect {

    public int layers = 1;
    private float perLayerDamage = 1;

    public Erosion(MobEffectType<?> effectType) {
        super(effectType);
    }

    public Erosion(float perLayerDamage) {
        this(LRSpecialMobEffect.EROSION.value());
        this.initTime(100);
        this.perLayerDamage = perLayerDamage;
    }

    @Override
    public void tick(LivingEntity livingEntity) {
        super.tick(livingEntity);
        if (livingEntity.level().getGameTime() % 20 == 0) {
            @Nullable Entity sourceEntity = getSourceEntity(livingEntity.level());
            if (sourceEntity instanceof LivingEntity sourceLiving) {
                livingEntity.hurt(livingEntity.level().damageSources().source(LRDamageTypes.EROSION, sourceEntity), perLayerDamage * layers);
                TreatmentBall treatmentBall = new TreatmentBall(sourceLiving.level(), sourceLiving, layers);
                treatmentBall.setPos(livingEntity.getEyePosition());
                sourceLiving.level().addFreshEntity(treatmentBall);
            }
        }
    }

    public Erosion getFinal(Erosion theOld) {
        this.layers = Math.min(theOld.layers + this.layers, 4);
        return this;
    }

    @Override
    public CompoundTag save() {
        CompoundTag compoundTag = super.save();
        compoundTag.putInt("Layers", layers);
        compoundTag.putFloat("PerLayerDamage", perLayerDamage);
        return compoundTag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        layers = compoundTag.getInt("Layers");
        perLayerDamage = compoundTag.getFloat("PerLayerDamage");

    }
}
