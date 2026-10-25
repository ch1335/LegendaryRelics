package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.api.FormatedOperation;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class TieredBonus extends FormatedOperation {


    public static final MapCodec<TieredBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.listOf().fieldOf("values").forGetter(TieredBonus::list),
            Format.CODEC.optionalFieldOf("format", Format.NORMAL).forGetter(bonus -> bonus.format)
    ).apply(instance, TieredBonus::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TieredBonus> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list()),
            TieredBonus::list,
            Format.STREAM_CODEC,
            bonus -> bonus.format,
            TieredBonus::new
    );

    public static final CalculatorArg.ArgType<Integer> TIER = new CalculatorArg.ArgType<>("tier");

    private final List<Float> list;

    public TieredBonus(List<Float> list) {
        this(list, Format.NORMAL);
    }

    public TieredBonus(List<Float> list, Format format) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("The entered list cannot be empty!");
        }
        this.list = list;
        this.format = format;
    }

    public List<Float> list() {
        return list;
    }

    public static TieredBonus of(List<Float> list) {
        return new TieredBonus(list);
    }

    public static TieredBonus of(List<Float> list, Format format) {
        return new TieredBonus(list, format);
    }

    @Override
    public float getValue(CalculatorArg calculatorArg) {
        int tier = Math.max(calculatorArg.getArgElse(TIER, 1), 1);
        if (tier >= list.size() + 1) {
            return list.getLast();
        }
        return list.get(tier - 1);
    }

    @Override
    public Component expand(CalculatorArg calculatorArg) {
        int tier = calculatorArg.getArgElse(TIER, 0);

        MutableComponent component = Component.empty();
        component.append("(");
        for (int i = 1; i <= list.size(); i++) {
            MutableComponent component1 = formatValue(list.get(i - 1));
            if (i == tier) {
                component1.withStyle(ChatFormatting.GRAY);
            }
            component.append(component1);
            if (i != list.size()) {
                component.append("|");
            }
        }
        component.append(")");
        return component;
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
        return CalculatorRegister.TIERED_BONUS.get();
    }
}
