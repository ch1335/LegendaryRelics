package com.chen1335.legendaryRelics.API;

public interface ILRItemExtension {
    default boolean isNoRightClickCooldown() {
        return false;
    }
}
