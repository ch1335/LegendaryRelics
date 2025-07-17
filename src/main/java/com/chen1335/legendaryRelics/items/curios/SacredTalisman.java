package com.chen1335.legendaryRelics.items.curios;

import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SacredTalisman extends LRCuriosBase {
    public SacredTalisman() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public Map<EffectType<?>, BaseEffect> getDefaultEffect() {
        return Map.of(LREquipmentEffectTypes.REDEMPTION.value(), LREquipmentEffectTypes.REDEMPTION.value().create(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

    }
}
