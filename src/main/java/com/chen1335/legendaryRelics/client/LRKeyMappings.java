package com.chen1335.legendaryRelics.client;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects.ShadowStrike;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * 客户端按键绑定。
 */
public final class LRKeyMappings {

    public static final KeyMapping SHADOW_STRIKE = new KeyMapping(
            "key.legendary_relics.shadow_strike",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.legendary_relics"
    );

    static {
        ShadowStrike.keyDisplay = LRKeyMappings::shadowStrike;
    }

    private LRKeyMappings() {
    }

    public static Component shadowStrike() {
        return Component.literal("[").append(SHADOW_STRIKE.getTranslatedKeyMessage()).append("]");
    }
}
