package com.chen1335.legendaryRelics.registers.items.weapons;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.common.EquipmentType;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.IChargeAbleItem;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.registers.equipmentEffects.weaponEffects.Ambush;
import com.chen1335.legendaryRelics.registers.items.LRSwordItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class ShadowDagger extends LRSwordItem implements IChargeAbleItem {
    public static Tier TIER = new SimpleTier(BlockTags.INCORRECT_FOR_IRON_TOOL, 512 + 256, 6.0F, 0, 14, () -> Ingredient.of(Tags.Items.BONES));

    public ShadowDagger() {
        super(TIER, new Item.Properties().rarity(Rarity.RARE).attributes(SwordItem.createAttributes(Tiers.NETHERITE, 2, -2F)));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(
                LREquipmentEffectTypes.BACKSTAB.value().create(1, EquipmentType.HAND),
                LREquipmentEffectTypes.AMBUSH.value().create(1, EquipmentType.HAND)
        );
    }

    @Override
    public int maxToolTipWith() {
        return 270;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        Ambush effect = EquipmentEffectAPI.getEffect(stack, LREquipmentEffectTypes.AMBUSH.value());
        if (effect != null) {
            effect.handleReleaseUsing(stack, level, livingEntity, timeLeft);
        }
    }

    public float getChargePercent(ItemStack stack, LivingEntity entity) {
        int maxChargeTick = getMaxChargeTick(stack);
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
    public int getMaxChargeTick(ItemStack itemStack) {
        return 40;
    }
}
