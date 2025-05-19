package com.chen1335.legendaryRelics.API.objects;

import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public class LRRarities {
    public static final EnumProxy<Rarity> DARK_GOLD = new EnumProxy<>(
            Rarity.class, -1, "legendary_relics:dark_gold", (UnaryOperator<Style>) style -> style.withColor(0xff8c00)
    );
}
