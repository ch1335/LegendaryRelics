package com.chen1335.legendaryRelics.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class LRClient {
    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
