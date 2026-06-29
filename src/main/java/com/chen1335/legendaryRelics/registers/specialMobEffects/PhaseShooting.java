package com.chen1335.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.AttributesGetter;
import com.chen1335.legendaryRelics.registers.specialMobEffects.common.StackAbleEffect;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class PhaseShooting extends StackAbleEffect {
    private static final ResourceLocation DRAW_SPEED = LegendaryRelics.id("twisted.draw_speed");
    private static final ResourceLocation ARROW_DAMAGE = LegendaryRelics.id("twisted.arrow_damage");

    private float drawSpeedPerStack;
    private float arrowDamagePerStack;

    public PhaseShooting(MobEffectType<?> effectType) {
        super(effectType);
        registerModifier(AttributesGetter.drawSpeed(), DRAW_SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, value -> value * drawSpeedPerStack);
        registerModifier(AttributesGetter.arrowDamage(), ARROW_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, value -> value * arrowDamagePerStack);
    }

    public PhaseShooting(float drawSpeedPerStack, float arrowDamagePerStack) {
        this(LRSpecialMobEffects.PHASE_SHOOTING.value());
        this.drawSpeedPerStack = drawSpeedPerStack;
        this.arrowDamagePerStack = arrowDamagePerStack;
    }

    @Override
    public void onAddOrUpdate(LivingEntity livingEntity) {
        super.onAddOrUpdate(livingEntity);
        addAttributeModifiers(livingEntity.getAttributes());
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        super.onRemove(livingEntity);
        removeAttributeModifiers(livingEntity.getAttributes());
    }

    public PhaseShooting getFinal(PhaseShooting theOld) {
        PhaseShooting aFinal = super.getFinal(theOld);
        aFinal.drawSpeedPerStack = drawSpeedPerStack;
        aFinal.arrowDamagePerStack = arrowDamagePerStack;
        return aFinal;
    }

    @Override
    public int getMaxStack() {
        return 10;
    }

    @Override
    public int getModifierAmplifier() {
        return stack;
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = super.save();
        tag.putFloat("drawSpeedPerStack", drawSpeedPerStack);
        tag.putFloat("arrowDamagePerStack", arrowDamagePerStack);
        return tag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        drawSpeedPerStack = compoundTag.getFloat("drawSpeedPerStack");
        arrowDamagePerStack = compoundTag.getFloat("arrowDamagePerStack");
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        drawSpeedPerStack = buffer.readFloat();
        arrowDamagePerStack = buffer.readFloat();
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(drawSpeedPerStack);
        buffer.writeFloat(arrowDamagePerStack);
    }
}
