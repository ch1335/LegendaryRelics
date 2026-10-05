package com.chen1335.dungeonLib;

import com.chen1335.specialEffectLib.API.objects.SEAttachmentTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

public class DungeonLib {
    public static final String MODID = "dungeon_lib";

    public static void init(IEventBus modEventBus, ModContainer modContainer) {
        SEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
    }
}
