package com.chen1335.legendaryRelics.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ComponentHolders {
    public static class Effects {
        public static Component infernoScorch() {
            return Component.translatable("legendary_relics.special_effect.inferno_scorch").withStyle(ChatFormatting.RED);
        }
    }

    public static class Misc {
        public static Component tieredBonus(Component arg) {
            return Component.translatable("legendary_relics.special_effect.inferno_scorch").withStyle(ChatFormatting.GOLD);
        }


    }
}
