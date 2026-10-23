package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.legendaryRelics.API.objects.LRRarities;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.OperationType;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DarkGoldUpdateArg implements OperationUnit {

    public static final MapCodec<DarkGoldUpdateArg> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CalculatorRegister.DISPATCH_CODEC.fieldOf("normal").forGetter(arg -> arg.a),
            CalculatorRegister.DISPATCH_CODEC.fieldOf("dark_gold").forGetter(arg -> arg.b)
    ).apply(instance, DarkGoldUpdateArg::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DarkGoldUpdateArg> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.a,
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.b,
            DarkGoldUpdateArg::new
    );


    private final OperationUnit a;
    private final OperationUnit b;

    private DarkGoldUpdateArg(OperationUnit a, OperationUnit b) {
        this.a = a;
        this.b = b;
    }


    @Override
    public float getValue(CalculatorArg calculatorArg) {
        @Nullable ItemStack itemStack = CalculatorArg.ArgType.THIS_ITEMS_STACK.getArg(calculatorArg);
        if (itemStack != null && itemStack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
            return b.getValue(calculatorArg);
        } else {
            return a.getValue(calculatorArg);
        }
    }

    @Override
    public Component toComponent(CalculatorArg calculatorArg) {
        @Nullable ItemStack itemStack = CalculatorArg.ArgType.THIS_ITEMS_STACK.getArg(calculatorArg);
        if (itemStack != null && itemStack.getRarity() == LRRarities.DARK_GOLD.getValue()) {
            return b.toComponent(calculatorArg).copy().withColor(0xff8c00);
        } else {
            return a.toComponent(calculatorArg);
        }
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
        return CalculatorRegister.DARK_GOLD_UPDATE.get();
    }

    public static DarkGoldUpdateArg of(OperationUnit a, OperationUnit b) {
        return new DarkGoldUpdateArg(a, b);
    }

    public static DarkGoldUpdateArg of(OperationUnit a) {
        return new DarkGoldUpdateArg(a, a);
    }

}
