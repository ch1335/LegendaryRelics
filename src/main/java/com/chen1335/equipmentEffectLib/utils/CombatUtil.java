package com.chen1335.equipmentEffectLib.utils;

import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas.LRPlayerData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

public class CombatUtil {
    public static boolean isAvailableTarget(LivingEntity attacker, LivingEntity target) {
        if (attacker == null) {
            return false;
        }

        boolean canAttack = target != attacker
                && target instanceof Enemy
                && target.isAlive()
                && !target.isSpectator()
                && !target.isAlliedTo(attacker)
                && attacker.canAttack(target);
        if (attacker instanceof Player player) {
            return canAttack || LRPlayerData.getData(player).isAttacking(target);
        }
        return canAttack;
    }

    public static boolean isStrictAttack(DamageSource damageSource) {
        if (!damageSource.isDirect()) {
            return false;
        }
        return damageSource.is(DamageTypes.PLAYER_ATTACK) || damageSource.is(DamageTypes.MOB_ATTACK);
    }

    public static boolean isLooseAttack(DamageSource damageSource) {
        return isStrictAttack(damageSource) || damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK);
    }
}
