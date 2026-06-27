package com.chen1335.equipmentEffectLib.common;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public record InfoHolder<T extends BaseEffect>(ItemStack itemStack, T effect) {
    public static final Codec<InfoHolder<? extends BaseEffect>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("itemStack").forGetter(InfoHolder::itemStack),
            BaseEffect.CODEC.fieldOf("effect").forGetter(InfoHolder::effect)
    ).apply(instance, InfoHolder::new));
}
