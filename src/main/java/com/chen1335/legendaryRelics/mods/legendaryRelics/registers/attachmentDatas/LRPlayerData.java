package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas;

import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.animation.PlayerAnimationState;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class LRPlayerData implements INBTSerializable<CompoundTag> {
    public boolean hasGiveBook = false;

    public boolean hasGiveCharmOfFreshStart = false;

    public int inCombatTime = 0;

    public IntSet attackingTarget = new IntArraySet();

    public PlayerAnimationState playerAnimationState = null;

    public final List<Object> invincibleSource = new ArrayList<>();
    public final List<Object> invinciblePushSource = new ArrayList<>();
    public static LRPlayerData getData(Player living) {
        return living.getData(LRAttachmentTypes.PLAYER_DATA.get());
    }

    public void addAttacking(Entity entity) {
        attackingTarget.add(entity.getId());
        inCombatTime = 600;
    }

    public boolean isAttacking(Entity entity) {
        return attackingTarget.contains(entity.getId());
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean("HasGiveBook", hasGiveBook);
        compoundTag.putBoolean("HasGiveCharmOfFreshStart", hasGiveCharmOfFreshStart);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        hasGiveBook = nbt.getBoolean("HasGiveBook");
        hasGiveCharmOfFreshStart = nbt.getBoolean("HasGiveCharmOfFreshStart");
    }

    public void tick(Player player) {
        if (!attackingTarget.isEmpty() && inCombatTime <= 0) {
            attackingTarget.clear();
        } else if (inCombatTime > 0) {
            inCombatTime--;
        }
        if (playerAnimationState != null) {
            playerAnimationState.tick(player);
        }
    }

    public void addInvincibleSource(Object o) {
        invincibleSource.add(o);
    }

    public boolean isInvincible() {
        return !invincibleSource.isEmpty();
    }

    public void removeInvincibleSource(Object o) {
        invincibleSource.remove(o);
    }

    public void addInvinciblePushSource(Object o) {
        invinciblePushSource.add(o);
    }

    public boolean isInvinciblePush() {
        return !invinciblePushSource.isEmpty();
    }
    public void removeInvinciblePushSource(Object o) {
        invinciblePushSource.remove(o);
    }
}
