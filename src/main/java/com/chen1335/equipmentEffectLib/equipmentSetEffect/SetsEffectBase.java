package com.chen1335.equipmentEffectLib.equipmentSetEffect;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SetsEffectBase {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetsEffectBase> STREAM_CODEC = ByteBufCodecs.registry(EERegisterTypes.SETS_EFFECT_TYPE_KEY);

    public void appendToolTip(ItemStack itemStack, Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, List<Component> list) {

    }
}
