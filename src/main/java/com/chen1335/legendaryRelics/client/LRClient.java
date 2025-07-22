package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class LRClient {
    public static final EquipmentEffectCooldownManager CLIENT_COOLDOWN_MANAGER = new EquipmentEffectCooldownManager();

    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static EquipmentEffectCooldownManager getClientCooldownManager() {
        return CLIENT_COOLDOWN_MANAGER;
    }
}
