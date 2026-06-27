package com.chen1335.legendaryRelics.utils.valueHolder;

public class DefaultValueHolder<T> implements IValueHolder<T> {
    private T value;
    private final T defaultValue;

    public DefaultValueHolder(T value, T defaultValue) {
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public DefaultValueHolder(T value) {
        this(value, value);
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public T geDefault() {
        return defaultValue;
    }

    @Override
    public void set(T v) {
        value = v;
    }
}
