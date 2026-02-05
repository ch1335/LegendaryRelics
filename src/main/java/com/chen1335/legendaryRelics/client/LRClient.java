package com.chen1335.legendaryRelics.client;

import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class LRClient {
    public static final EquipmentEffectCooldownManager CLIENT_COOLDOWN_MANAGER = new EquipmentEffectCooldownManager();

    public static Map<SetEffect, Integer> ENTITY_SETS_EFFECT_DATA = new HashMap<>();

    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static EquipmentEffectCooldownManager getClientCooldownManager() {
        return CLIENT_COOLDOWN_MANAGER;
    }
}
