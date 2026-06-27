package com.chen1335.legendaryRelics.utils.valueHolder;

public interface IValueHolder<T> {
    T get();

    T geDefault();

    void set(T v);
}
