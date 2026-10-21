package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.IRSDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LRDamageTypeTagsProvider extends DamageTypeTagsProvider {
    public LRDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, LegendaryRelics.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_COOLDOWN).
                add(
                        LRDamageTypes.EROSION,
                        LRDamageTypes.FLYING_REAPER,
                        LRDamageTypes.INFERNO_SCORCH,
                        LRDamageTypes.FLYING_KNIFE,
                        LRDamageTypes.CHAIN_LIGHTNING,
                        LRDamageTypes.FROST_SHARD,
                        LRDamageTypes.ICICLE,
                        LRDamageTypes.ORBITING_FIRE_CHAKRAM,
                        LRDamageTypes.ICE_THORN,
                        IRSDamageTypes.SOUL_MISSILE
                );

        this.tag(DamageTypeTags.IS_PLAYER_ATTACK).
                add(
                        LRDamageTypes.FLYING_KNIFE
                );


        this.tag(Tags.DamageTypes.IS_MAGIC)
                .add(LRDamageTypes.EROSION);

        // 灵魂飞弹刻意只打 is_physical，不打 is_magic：
        // 套装的叠层/发射条件是「造成法术伤害」，飞弹若也算法术伤害会自我触发导致无限递归。
        this.tag(Tags.DamageTypes.IS_PHYSICAL)
                .add(
                        LRDamageTypes.FLYING_REAPER,
                        LRDamageTypes.INFERNO_SCORCH,
                        LRDamageTypes.FLYING_KNIFE,
                        LRDamageTypes.FROST_SHARD,
                        LRDamageTypes.ICICLE,
                        LRDamageTypes.ORBITING_FIRE_CHAKRAM,
                        LRDamageTypes.ICE_THORN,
                        IRSDamageTypes.SOUL_MISSILE
                );

        this.tag(DamageTypeTags.IS_PROJECTILE)
                .add(
                        LRDamageTypes.FLYING_REAPER,
                        LRDamageTypes.FLYING_KNIFE,
                        LRDamageTypes.FROST_SHARD,
                        LRDamageTypes.ICICLE
                );

        // 霜锥与冰锥都是投掷物，沿用原版三叉戟所在的另外两个标签
        this.tag(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS)
                .add(
                        LRDamageTypes.FROST_SHARD,
                        LRDamageTypes.ORBITING_FIRE_CHAKRAM,
                        LRDamageTypes.ICICLE
                );

        this.tag(DamageTypeTags.PANIC_CAUSES)
                .add(
                        LRDamageTypes.FROST_SHARD,
                        LRDamageTypes.ORBITING_FIRE_CHAKRAM,
                        LRDamageTypes.ICICLE
                );

        // 连锁闪电与灵魂飞弹都是"附带"的伤害，不应该把目标击退
        this.tag(DamageTypeTags.NO_KNOCKBACK)
                .add(
                        LRDamageTypes.EROSION,
                        LRDamageTypes.FLYING_REAPER,
                        LRDamageTypes.INFERNO_SCORCH,
                        LRDamageTypes.CHAIN_LIGHTNING,
                        LRDamageTypes.ORBITING_FIRE_CHAKRAM,
                        IRSDamageTypes.SOUL_MISSILE
                );
    }
}
