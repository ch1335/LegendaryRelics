package com.chen1335.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.TreatmentBall;
import com.chen1335.legendaryRelics.registers.specialMobEffects.common.StackAbleEffect;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;

public class Erosion extends StackAbleEffect {

    private float perLayerDamage = 1;

    private double armorReducePerLayer = 0.03F;

    public Erosion(MobEffectType<?> effectType) {
        super(effectType);
        registerModifier(Attributes.ARMOR, "erosion_armor_mdofier", AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, value -> - value * armorReducePerLayer);
    }

    public Erosion(float perLayerDamage, float armorReducePerLayer) {
        this(LRSpecialMobEffects.EROSION.value());
        this.perLayerDamage = perLayerDamage;
        this.armorReducePerLayer = FinalCalculator.castToDoubleStrict(armorReducePerLayer);
    }

    @Override
    public void tick(LivingEntity livingEntity) {
        super.tick(livingEntity);
        if (livingEntity.level().getGameTime() % 20 == 0) {
            @Nullable Entity sourceEntity = getSourceEntity(livingEntity.level());
            if (sourceEntity instanceof LivingEntity sourceLiving) {
                livingEntity.hurt(livingEntity.level().damageSources().source(LRDamageTypes.EROSION, sourceEntity), perLayerDamage * stack);
                TreatmentBall treatmentBall = new TreatmentBall(sourceLiving.level(), sourceLiving, stack);
                treatmentBall.setPos(livingEntity.getEyePosition());
                sourceLiving.level().addFreshEntity(treatmentBall);
            }
        }
    }

    public Erosion getFinal(Erosion theOld) {
        Erosion aFinal = super.getFinal(theOld);
        aFinal.perLayerDamage = perLayerDamage;
        aFinal.armorReducePerLayer = armorReducePerLayer;
        return aFinal;
    }

    @Override
    public int getMaxStack() {
        return 5;
    }

    @Override
    public CompoundTag save() {
        CompoundTag compoundTag = super.save();
        compoundTag.putFloat("PerLayerDamage", perLayerDamage);
        compoundTag.putDouble("ArmorReducePerLayer", armorReducePerLayer);
        return compoundTag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        perLayerDamage = compoundTag.getFloat("PerLayerDamage");
        armorReducePerLayer = compoundTag.getDouble("ArmorReducePerLayer");
    }

    @Override
    protected void onStackChange(LivingEntity livingEntity) {
        addAttributeModifiers(livingEntity.getAttributes());
    }

    @Override
    public void onAddOrUpdate(LivingEntity livingEntity) {
        addAttributeModifiers(livingEntity.getAttributes());
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        removeAttributeModifiers(livingEntity.getAttributes());
    }

    @Override
    public int getModifierAmplifier() {
        return stack;
    }
}
