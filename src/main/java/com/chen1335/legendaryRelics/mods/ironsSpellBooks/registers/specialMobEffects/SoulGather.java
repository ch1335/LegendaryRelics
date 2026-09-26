package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.specialMobEffects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.common.StackAbleEffect;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

/**
 * 灵魂套装的叠层效果：每层提供法术强度与法术冷却缩短，叠满 {@link #getMaxStack()} 层后解锁飞弹。
 */
public class SoulGather extends StackAbleEffect {
    private static final ResourceLocation SPELL_POWER = LegendaryRelics.id("soul.spell_power");
    private static final ResourceLocation COOLDOWN_REDUCTION = LegendaryRelics.id("soul.cooldown_reduction");

    private float spellPowerPerStack;
    private float cooldownReductionPerStack;

    public SoulGather(MobEffectType<?> effectType) {
        super(effectType);
        registerModifier(AttributeRegistry.SPELL_POWER, SPELL_POWER, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, value -> value * spellPowerPerStack);
        registerModifier(AttributeRegistry.COOLDOWN_REDUCTION, COOLDOWN_REDUCTION, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, value -> value * cooldownReductionPerStack);
    }

    public SoulGather(float spellPowerPerStack, float cooldownReductionPerStack) {
        this(ISSRegisters.SpecialMobEffect.SOUL_GATHER.value());
        this.spellPowerPerStack = spellPowerPerStack;
        this.cooldownReductionPerStack = cooldownReductionPerStack;
    }

    @Override
    public void onAddOrUpdate(LivingEntity livingEntity) {
        super.onAddOrUpdate(livingEntity);
        addAttributeModifiers(livingEntity.getAttributes());
    }

    @Override
    protected void onStackChange(LivingEntity livingEntity) {
        super.onStackChange(livingEntity);
        addAttributeModifiers(livingEntity.getAttributes());
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        super.onRemove(livingEntity);
        removeAttributeModifiers(livingEntity.getAttributes());
    }

    public SoulGather getFinal(SoulGather theOld) {
        SoulGather aFinal = super.getFinal(theOld);
        aFinal.spellPowerPerStack = spellPowerPerStack;
        aFinal.cooldownReductionPerStack = cooldownReductionPerStack;
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
        tag.putFloat("spellPowerPerStack", spellPowerPerStack);
        tag.putFloat("cooldownReductionPerStack", cooldownReductionPerStack);
        return tag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        spellPowerPerStack = compoundTag.getFloat("spellPowerPerStack");
        cooldownReductionPerStack = compoundTag.getFloat("cooldownReductionPerStack");
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        spellPowerPerStack = buffer.readFloat();
        cooldownReductionPerStack = buffer.readFloat();
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(spellPowerPerStack);
        buffer.writeFloat(cooldownReductionPerStack);
    }
}
