package com.chen1335.equipmentEffectLib.utils;

public final class Cast {
    public static <T> T cast(final Object o) {
        if (o == null) {
            return null;
        }
        @SuppressWarnings("unchecked") final T t = (T) o;
        return t;
    }

    private Cast() {
    }
}
