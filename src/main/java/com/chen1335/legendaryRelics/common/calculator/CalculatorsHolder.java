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

    public static Map<LocateInfo, FinalCalculator> getCalculators() {
        return CALCULATORS_HOLDER_MAP;
    }

    public record LocateInfo(String className, String fieldName) {

        public LocateInfo(Class<?> className, String fieldName) {
            this(className.getName(), fieldName);
        }

        public static final StreamCodec<ByteBuf, LocateInfo> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                LocateInfo::className,
                ByteBufCodecs.STRING_UTF8,
                LocateInfo::fieldName,
                LocateInfo::new
        );

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocateInfo that = (LocateInfo) o;
            return Objects.equals(className, that.className) && Objects.equals(fieldName, that.fieldName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(className, fieldName);
        }
    }
}
