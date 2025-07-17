package com.chen1335.legendaryRelics.items.weaapons;

import com.chen1335.legendaryRelics.items.LRSwordItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import org.jetbrains.annotations.NotNull;

public class TestWeapon extends LRSwordItem {

    private static final Tier tier = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 0, 9F, 1, 10, () -> Ingredient.EMPTY);

    public TestWeapon() {
        super(tier, new Properties().stacksTo(1).attributes(SwordItem.createAttributes(tier, 8, -2.4F)));
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (attacker instanceof Player player) {

        }
        return true;
    }
}
