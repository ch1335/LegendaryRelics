package com.chen1335.legendaryRelics.utils.valueHolder;

public class ValueHolderWrapper<T> implements IValueHolder<T> {
    private IValueHolder<T> innerHolder;

    public ValueHolderWrapper(IValueHolder<T> innerHolder) {
        this.innerHolder = innerHolder;
    }

    public void setInnerHolder(IValueHolder<T> innerHolder) {
        this.innerHolder = innerHolder;
    }

    @Override
    public T get() {
        return innerHolder.get();
    }

    @Override
    public T geDefault() {
        return innerHolder.geDefault();
    }

    @Override
    public void set(T v) {
        innerHolder.set(v);
    }
}
