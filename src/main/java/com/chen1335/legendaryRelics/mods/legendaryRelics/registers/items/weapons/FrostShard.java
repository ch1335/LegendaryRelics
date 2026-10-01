package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.API.IChargeAbleItem;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.projectiles.misc.ThrownFrostShard;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRSwordItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * 霜锥：可以像三叉戟一样蓄力投掷的近战武器。
 *
 * <p>与三叉戟的区别在于投掷代价：投掷**不消耗物品**，只扣 1 点耐久并进入 10 秒冷却，
 * 因此投出的实体也没有拾取机制（见 {@link ThrownFrostShard}）。
 */
public class FrostShard extends LRSwordItem implements IChargeAbleItem {

    /** 蓄力到可以投掷所需的最短时间（tick），与三叉戟一致。 */
    public static final int THROW_THRESHOLD_TIME = 10;

    /** 投掷初速，与三叉戟一致。 */
    public static final float SHOOT_POWER = 2.5F;

    /** 投掷基础伤害，与三叉戟一致。 */
    public static final float THROW_DAMAGE = 8.0F;

    /** 投掷后的冷却时间（tick），10 秒。 */
    public static final int THROW_COOLDOWN = 200;

    public FrostShard() {
        super(Tier.Weapon.FROST_SHARD, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        // 只剩最后 1 点耐久时不允许投掷，避免投出去直接把物品用坏
        if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        int chargeTime = this.getUseDuration(stack, livingEntity) - timeLeft;
        if (chargeTime < THROW_THRESHOLD_TIME) {
            return;
        }

        if (!level.isClientSide) {
            ThrownFrostShard shard = new ThrownFrostShard(player, level, stack);
            shard.setPos(player.getEyePosition());
            shard.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, SHOOT_POWER, 1.0F);
            shard.setBaseDamage(THROW_DAMAGE);
            level.addFreshEntity(shard);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0F, 1.0F);

            // 投掷不消耗物品，只扣 1 点耐久
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
        }

        // 投掷后进入 10 秒冷却
        player.getCooldowns().addCooldown(this, THROW_COOLDOWN);
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public boolean isNoRightClickCooldown() {
        return true;
    }


    @Override
    public int getMaxChargeTick(ItemStack itemStack, LivingEntity user) {
        return THROW_THRESHOLD_TIME;
    }
}
