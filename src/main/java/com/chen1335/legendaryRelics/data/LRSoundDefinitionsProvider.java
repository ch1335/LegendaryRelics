package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.API.objects.LRSounds;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class LRSoundDefinitionsProvider extends SoundDefinitionsProvider {

    protected LRSoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, LegendaryRelics.MODID, helper);
    }

    @Override
    public void registerSounds() {
        add(LRSounds.CHAIN_LIGHTNING.getKey().location(), SoundDefinition.definition().with(sound(LegendaryRelics.id("misc/chain_lightning"))));
        add(LRSounds.HOLY_BARRIER_BREAK.getKey().location(), SoundDefinition.definition().with(sound(LegendaryRelics.id("misc/holy_barrier_break"))));
    }
}
