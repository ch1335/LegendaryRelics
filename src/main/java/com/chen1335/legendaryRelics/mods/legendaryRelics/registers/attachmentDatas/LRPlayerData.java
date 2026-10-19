package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas;

import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class LRPlayerData implements INBTSerializable<CompoundTag> {
    public boolean hasGiveBook = false;

    public boolean hasGiveCharmOfFreshStart = false;

    public int inCombatTime = 0;

    public IntSet attackingTarget = new IntArraySet();

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
    }
}
