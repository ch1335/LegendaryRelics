package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.AttributeBoostInNether;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NetherTalisman extends LRCuriosBase {

    public static final ResourceLocation NETHER_TALISMAN_ATTRIBUTE_MULTIPLIER = LegendaryRelics.id("nether_talisman_attribute_multiplier");


    public NetherTalisman() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffect() {
        return List.of(new AttributeBoostInNether(1));
    }
}
