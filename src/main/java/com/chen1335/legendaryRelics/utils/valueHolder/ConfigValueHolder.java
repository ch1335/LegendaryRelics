package com.chen1335.legendaryRelics.utils.valueHolder;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigValueHolder<T> implements IValueHolder<T> {

    private final ModConfigSpec.ConfigValue<T> configValue;

    ConfigValueHolder(ModConfigSpec.ConfigValue<T> configValue) {
        this.configValue = configValue;
    }

    @Override
    public T get() {
        return configValue.get();
    }

    @Override
    public T geDefault() {
        return configValue.getDefault();
    }

    @Override
    public void set(T v) {
        configValue.set(v);
    }

    public static <T> ConfigValueHolder<T> of(ModConfigSpec.ConfigValue<T> configValue) {
        return new ConfigValueHolder<>(configValue);
    }
}
