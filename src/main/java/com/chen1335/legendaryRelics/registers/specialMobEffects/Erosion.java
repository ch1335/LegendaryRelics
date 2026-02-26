package com.chen1335.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffect;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.registers.entities.projectiles.misc.TreatmentBall;
import com.chen1335.legendaryRelics.utils.LRUtil;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Erosion extends TimeLimitEffect {

    public int layers = 1;
    private float perLayerDamage = 1;

    private double armorReducePerLayer = 0.03F;
    private ResourceLocation modifierId = LRUtil.randomLocation(10);

    public Erosion(MobEffectType<?> effectType) {
        super(effectType);
    }

    public Erosion(float perLayerDamage, float armorReducePerLayer) {
        this(LRSpecialMobEffect.EROSION.value());
        this.initTime(100);
        this.perLayerDamage = perLayerDamage;
        this.armorReducePerLayer = FinalCalculator.castToDoubleStrict(armorReducePerLayer);
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
        this.layers = Math.min(theOld.layers + this.layers, 5);
        this.modifierId = theOld.modifierId;
        return this;
    }

    @Override
    public CompoundTag save() {
        CompoundTag compoundTag = super.save();
        compoundTag.putInt("Layers", layers);
        compoundTag.putFloat("PerLayerDamage", perLayerDamage);
        compoundTag.putString("modifierId", modifierId.toString());
        return compoundTag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        layers = compoundTag.getInt("Layers");
        perLayerDamage = compoundTag.getFloat("PerLayerDamage");
        modifierId = ResourceLocation.parse(compoundTag.getString("modifierId"));
    }

    @Override
    public void onAddOrUpdate(LivingEntity livingEntity) {
        AttributeInstance instance = livingEntity.getAttribute(Attributes.ARMOR);
        if (instance != null) {
            instance.removeModifier(modifierId);
            instance.addPermanentModifier(new AttributeModifier(modifierId, -(armorReducePerLayer * layers), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        AttributeInstance instance = livingEntity.getAttribute(Attributes.ARMOR);
        if (instance != null) {
            instance.removeModifier(modifierId);
        }
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        layers = buffer.readInt();
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeInt(layers);
    }
}
