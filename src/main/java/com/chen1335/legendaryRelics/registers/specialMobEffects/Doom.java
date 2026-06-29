package com.chen1335.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.specialMobEffects.common.StackAbleEffect;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public class Doom extends StackAbleEffect {
    private static final ResourceLocation ATTACK_RANGE = LegendaryRelics.id("inferno.attack_range");
    private static final ResourceLocation ATTACK_DAMAGE = LegendaryRelics.id("inferno.attack_damage");

    private float attackRangePerStack;
    private float damagePerStack;

    public Doom(MobEffectType<?> effectType) {
        super(effectType);
        registerModifier(Attributes.ENTITY_INTERACTION_RANGE, ATTACK_RANGE, AttributeModifier.Operation.ADD_VALUE, value -> value * attackRangePerStack);
        registerModifier(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, value -> value * damagePerStack);
    }

    public Doom(float attackRangePerStack, float damagePerStack) {
        this(LRSpecialMobEffects.DOOM.value());
        this.attackRangePerStack = attackRangePerStack;
        this.damagePerStack = damagePerStack;
    }

    @Override
    protected void onStackChange(LivingEntity livingEntity) {
        super.onStackChange(livingEntity);
        addAttributeModifiers(livingEntity.getAttributes());
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


    public Doom getFinal(Doom theOld) {
        Doom aFinal = super.getFinal(theOld);
        aFinal.damagePerStack = damagePerStack;
        aFinal.attackRangePerStack = attackRangePerStack;
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
        tag.putFloat("attackRangePerStack", attackRangePerStack);
        tag.putFloat("damagePerStack", damagePerStack);
        return tag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        attackRangePerStack = compoundTag.getFloat("attackRangePerStack");
        damagePerStack = compoundTag.getFloat("damagePerStack");
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        attackRangePerStack = buffer.readFloat();
        damagePerStack = buffer.readFloat();
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(attackRangePerStack);
        buffer.writeFloat(damagePerStack);
    }
}
