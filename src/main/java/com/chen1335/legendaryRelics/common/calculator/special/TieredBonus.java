package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record TieredBonus(List<Float> list) implements Unit {
    public static final StreamCodec<RegistryFriendlyByteBuf, TieredBonus> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list()),
            TieredBonus::list,
            TieredBonus::new
    );


    public TieredBonus {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("The entered list cannot be empty!");
        }
    }

    public static final CalculatorArg.ArgType<Integer> TIER = new CalculatorArg.ArgType<>("tier");

    public static Unit of(List<Float> list) {
        return new TieredBonus(list);
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        int tier = Math.max(calculatorArg.getArgElse(TIER, 0), 0);
        if (tier >= list.size()) {
            return list.getLast();
        }
        return list.get(tier);
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        int tier = calculatorArg.getArgElse(TIER, 0);

        MutableComponent component = Component.empty();
        component.append("(");
        for (int i = 0; i < list.size(); i++) {
            MutableComponent component1 = Component.literal(FinalCalculator.format(list.get(i), 3));
            if (i == tier) {
                component1.withStyle(ChatFormatting.GRAY);
            }
            component.append(component1);
            if (i != list.size() - 1) {
                component.append("|");
            }
        }
        component.append(")");
        return component;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return STREAM_CODEC;
    }
}
