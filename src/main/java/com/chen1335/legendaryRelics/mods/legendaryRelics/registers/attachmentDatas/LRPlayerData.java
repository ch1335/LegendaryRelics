package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas;

import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.animation.PlayerAnimationState;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class LRPlayerData implements INBTSerializable<CompoundTag> {
    public boolean hasGiveBook = false;

    public boolean hasGiveCharmOfFreshStart = false;

    public int inCombatTime = 0;

    public IntSet attackingTarget = new IntArraySet();

    private int lastedTargetId = -1;

    public PlayerAnimationState playerAnimationState = null;

    public final Set<Object> invincibleSource = Collections.newSetFromMap(new IdentityHashMap<>());
    public final Set<Object> invinciblePushSource = Collections.newSetFromMap(new IdentityHashMap<>());

    public static LRPlayerData getData(Player living) {
        return living.getData(LRAttachmentTypes.PLAYER_DATA.get());
    }

    public void addAttacking(Entity entity) {
        attackingTarget.add(entity.getId());
        lastedTargetId = entity.getId();
        inCombatTime = 600;
    }

    public boolean isAttacking(Entity entity) {
        return attackingTarget.contains(entity.getId());
    }

    public @Nullable Entity getLastedTarget(Level level) {
        Entity entity = lastedTargetId == -1 ? null : level.getEntity(lastedTargetId);
        if (entity == null) {
            lastedTargetId = -1;
        }
        return entity;
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
            lastedTargetId = -1;
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
