package com.chen1335.legendaryRelics.common.calculator;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CalculatorsHolder {
    private static final Map<LocateInfo, FinalCalculator> CALCULATORS_HOLDER_MAP = new HashMap<>();

    public static void register(LocateInfo locateInfo, FinalCalculator finalCalculator) {
        CALCULATORS_HOLDER_MAP.putIfAbsent(locateInfo, finalCalculator);
    }

    public static FinalCalculator registerKubejs(String id, FinalCalculator finalCalculator) {
        LocateInfo locateInfo = LocateInfo.kubejsLocateInfo(id);
        FinalCalculator old = CALCULATORS_HOLDER_MAP.get(locateInfo);
        if (old != null) {
            old.define(finalCalculator);
            return old;
        }
        CALCULATORS_HOLDER_MAP.putIfAbsent(locateInfo, finalCalculator);
        return finalCalculator;
    }

    public static FinalCalculator getKubejs(String id) {
        LocateInfo locateInfo = LocateInfo.kubejsLocateInfo(id);
        return CALCULATORS_HOLDER_MAP.get(locateInfo);
    }

    public static Map<LocateInfo, FinalCalculator> getCalculators() {
        return CALCULATORS_HOLDER_MAP;
    }

    public record LocateInfo(String className, String fieldName) {

        public LocateInfo(Class<?> className, String fieldName) {
            this(className.getName(), fieldName);
        }

        public static LocateInfo kubejsLocateInfo(String name) {
            return new LocateInfo("kubejs", name);
        }

        public static final StreamCodec<ByteBuf, LocateInfo> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                LocateInfo::className,
                ByteBufCodecs.STRING_UTF8,
                LocateInfo::fieldName,
                LocateInfo::new
        );
    }
}
