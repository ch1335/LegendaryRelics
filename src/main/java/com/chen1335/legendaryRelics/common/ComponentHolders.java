package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.utils.LRColors;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;

public class ComponentHolders {
    public static class Effects {
        public static Component infernoScorch() {
            return Component.translatable("legendary_relics.special_effect.inferno_scorch").withStyle(ChatFormatting.RED);
        }

        public static Component soulGather() {
            return Component.translatable("legendary_relics.stack.soul_gather").withStyle(ChatFormatting.AQUA);
        }
    }

    public static class Attributes {
        public static MutableComponent attribute(Holder<Attribute> attribute, Component value, AttributeModifier.Operation operation) {
            String op = operation == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL ? "×" : "+";
            MutableComponent component = Component.empty();
            component.append(op);
            component.append(value);
            component.append(Component.translatable(attribute.value().getDescriptionId()).withStyle(attribute.value().getStyle(true)));
            return component;
        }
    }

    public static class Combat {
        public static Component physicalDamage(Component component) {
            return Component.translatable("legendary_relics.damage_type.format", component, physicalDamage()).withStyle(LRColors.Component.RED.getStyle());
        }

        public static Component magicDamage(Component component) {
            return Component.translatable("legendary_relics.damage_type.format", component, magicDamage()).withStyle(LRColors.Component.BLUE.getStyle());
        }

        public static Component physicalDamage() {
            return Component.translatable("legendary_relics.damage_type.physical_damage");
        }

        public static Component magicDamage() {
            return Component.translatable("legendary_relics.damage_type.magic_damage");
        }
    }
}
