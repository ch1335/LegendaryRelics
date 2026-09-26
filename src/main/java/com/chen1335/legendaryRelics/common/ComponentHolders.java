package com.chen1335.legendaryRelics.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ComponentHolders {
    public static class Effects {
        public static Component infernoScorch() {
            return Component.translatable("legendary_relics.special_effect.inferno_scorch").withStyle(ChatFormatting.RED);
        }

        public static Component soulGather() {
            return Component.translatable("legendary_relics.stack.soul_gather").withStyle(ChatFormatting.AQUA);
        }
    }

    public static class Misc {


    }

    public static class Combat {
        public static Component physicalDamage(Component component) {
            return Component.translatable("legendary_relics.damage_type.format", component, physicalDamage()).withStyle(ChatFormatting.GOLD);
        }

        public static Component magicDamage(Component component) {
            return Component.translatable("legendary_relics.damage_type.format", component, physicalDamage()).withStyle(ChatFormatting.GOLD);
        }
        public static Component physicalDamage() {
            return Component.translatable("legendary_relics.damage_type.physical_damage").withStyle(ChatFormatting.RED);
        }

        public static Component magicDamage() {
            return Component.translatable("legendary_relics.damage_type.magic_damage").withStyle(ChatFormatting.RED);
        }
    }
}
