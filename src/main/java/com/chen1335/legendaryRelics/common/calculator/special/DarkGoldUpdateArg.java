package com.chen1335.legendaryRelics.common.calculator.special;

import com.chen1335.legendaryRelics.API.objects.LRRarities;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.CalculatorRegister;
import com.chen1335.legendaryRelics.common.calculator.api.Unit;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DarkGoldUpdateArg implements Unit {

    public static final StreamCodec<RegistryFriendlyByteBuf, DarkGoldUpdateArg> STREAM_CODEC = StreamCodec.composite(
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.a,
            CalculatorRegister.DISPATCH_STREAM_CODEC,
            value -> value.b,
            DarkGoldUpdateArg::new
    );


    private final Unit a;
    private final Unit b;

    private DarkGoldUpdateArg(Unit a, Unit b) {
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
    public StreamCodec<RegistryFriendlyByteBuf, ? extends Unit> getStreamCodec() {
        return STREAM_CODEC;
    }

    public static DarkGoldUpdateArg of(Unit a, Unit b) {
        return new DarkGoldUpdateArg(a, b);
    }

    public static DarkGoldUpdateArg of(Unit a) {
        return new DarkGoldUpdateArg(a, a);
    }

}
