package com.chen1335.equipmentEffectLib.equipmentSetEffect;

import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntitySetsEffectData;
import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.special.TieredBonus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SetEffect {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetEffect> STREAM_CODEC = ByteBufCodecs.registry(EERegisterTypes.SETS_EFFECT_TYPE_KEY);

    public void appendToolTip(ItemStack itemStack, Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, List<Component> list) {

    }

    public EffectInstance createInstance(int piece) {
        return new EffectInstance(piece);
    }

    public CalculatorArg buildArg(LivingEntity living) {
        if (living == null) {
            return CalculatorArg.emptyArg();
        }
        CalculatorArg args = CalculatorArg.simpleArg(living);
        int piece = getPiece(living);
        args.putArg(TieredBonus.TIER, piece);
        return args;
    }

    public int getPiece(LivingEntity living) {
        if (living == null) {
            return 0;
        }
        if (living.level().isClientSide) {
            return LRClient.ENTITY_SETS_EFFECT_DATA.getOrDefault(this, 0);
        }
        return EntitySetsEffectData.getPiece(living, this);
    }
}
