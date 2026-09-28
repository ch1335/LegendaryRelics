package com.chen1335.legendaryRelics.common;

import com.chen1335.legendaryRelics.API.LRWeaponTier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.function.Supplier;

public class LRSimpleTier extends SimpleTier implements LRWeaponTier {
    private final float attackSpeed;

    public LRSimpleTier(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus, float attackSpeed, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        super(incorrectBlocksForDrops, uses, speed, attackDamageBonus, enchantmentValue, repairIngredient);
        this.attackSpeed = attackSpeed;
    }


    @Override
    public float getAttackSpeed() {
        return attackSpeed;
    }
}
