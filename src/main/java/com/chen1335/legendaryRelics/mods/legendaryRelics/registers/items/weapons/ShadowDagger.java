package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.IChargeAbleItem;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.weaponEffects.Ambush;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRMeleeWeaponBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class ShadowDagger extends LRMeleeWeaponBase implements IChargeAbleItem {

    public ShadowDagger() {
        super(Tier.Weapon.SHADOW_DAGGER, new Item.Properties().rarity(Rarity.RARE));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(
                LREquipmentEffectTypes.BACKSTAB.value().create(1, EquipmentTypes.MAIN_HAND),
                LREquipmentEffectTypes.AMBUSH.value().create(1, EquipmentTypes.MAIN_HAND)
                );
    }


    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        Ambush effect = EquipmentEffectAPI.getEffect(stack, LREquipmentEffectTypes.AMBUSH.value());
        if (effect != null && !livingEntity.level().isClientSide) {
            effect.handleReleaseUsing(stack, level, livingEntity, timeLeft);
        }
    }

    public float getChargePercent(ItemStack stack, LivingEntity entity) {
        int maxChargeTick = getMaxChargeTick(stack, entity);
        int chargeTick = Math.min(maxChargeTick, getUseDuration(stack, entity) - entity.getUseItemRemainingTicks());
        return (float) chargeTick / maxChargeTick;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return itemAbility == ItemAbilities.SWORD_DIG;
    }

    @Override
    public int getMaxChargeTick(ItemStack itemStack, LivingEntity user) {
        Ambush effect = EquipmentEffectAPI.getEffect(itemStack, LREquipmentEffectTypes.AMBUSH.value());
        if (effect != null) {
           return effect.getMaxChargeTick(itemStack,user);
        }
        return 0;
    }
}
