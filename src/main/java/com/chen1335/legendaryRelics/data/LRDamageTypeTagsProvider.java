package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.LegendaryRelics;
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
        this.tag(DamageTypeTags.BYPASSES_COOLDOWN).add(LRDamageTypes.EROSION);

        this.tag(Tags.DamageTypes.IS_MAGIC).add(LRDamageTypes.EROSION);

        this.tag(DamageTypeTags.NO_KNOCKBACK).add(LRDamageTypes.EROSION);
    }
}
