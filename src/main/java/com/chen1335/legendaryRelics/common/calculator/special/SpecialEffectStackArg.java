package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.common.StackAbleEffect;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import com.chen1335.specialEffectLib.API.objects.RegisterTypes;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

/**
 * 读取实体身上某个可叠层特殊效果的当前层数（如隐匿）。
 */
public class SpecialEffectStackArg implements OperationUnit {

    public static final MapCodec<SpecialEffectStackArg> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("effect").forGetter(arg -> arg.effectId)
    ).apply(instance, SpecialEffectStackArg::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpecialEffectStackArg> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            arg -> arg.effectId,
            SpecialEffectStackArg::new
    );

    private final ResourceLocation effectId;

    public SpecialEffectStackArg(ResourceLocation effectId) {
        this.effectId = effectId;
    }

    public SpecialEffectStackArg(MobEffectType<?> effectType) {
        this(RegisterTypes.SPECIAL_EFFECT_TYPE.getKey(effectType));
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        LivingEntity entity = CalculatorArg.ArgType.THIS_ENTITY.getArg(calculatorArg);
        if (entity == null) {
            return 0;
        }
        MobEffectType<?> effectType = RegisterTypes.SPECIAL_EFFECT_TYPE.get(this.effectId);
        if (effectType == null) {
            return 0;
        }
        Optional<? extends SpecialMobEffect> effect = SpecialEffectAPI.getEffect(entity, entity.getUUID(), effectType);
        if (effect.isPresent() && effect.get() instanceof StackAbleEffect stackAble) {
            return stackAble.getStack();
        }
        return 0;
    }

    @Override
    public Component expand(CalculatorArg calculatorArg) {
        return Component.literal(Integer.toString((int) getValue(calculatorArg)));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends OperationUnit> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MapCodec<? extends OperationUnit> getCodec() {
        return CODEC;
    }

    @Override
    public OperationType getType() {
        return CalculatorRegister.SPECIAL_EFFECT_STACK.get();
    }

    public static SpecialEffectStackArg of(MobEffectType<?> effectType) {
        return new SpecialEffectStackArg(effectType);
    }

    public static SpecialEffectStackArg lurk() {
        return new SpecialEffectStackArg(LRSpecialMobEffects.LURK.getId());
    }
}
