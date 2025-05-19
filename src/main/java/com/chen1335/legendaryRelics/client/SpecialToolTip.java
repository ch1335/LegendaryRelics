package com.chen1335.legendaryRelics.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;

public class SpecialToolTip {
    public static double getClientPlayerAttribute(Holder<Attribute> attributeHolder) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return 0;
        }

        return player.getAttributeValue(attributeHolder);
    }
}
