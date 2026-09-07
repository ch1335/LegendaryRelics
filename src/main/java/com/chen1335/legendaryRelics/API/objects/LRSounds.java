package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LRSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
            Registries.SOUND_EVENT, LegendaryRelics.MODID
    );

    public static final Holder<SoundEvent> CHAIN_LIGHTNING = registerSound("chain_lightning");

    private static Holder<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(LegendaryRelics.id(name)));
    }

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
