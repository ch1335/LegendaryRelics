package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.attachmentDatas.LREntityData;
import com.chen1335.legendaryRelics.registers.attachmentDatas.LRProjectileData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class LRAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, LegendaryRelics.MODID);

    public static final Supplier<AttachmentType<LREntityData>> ENTITY_DATA = ATTACHMENT_TYPES.register(
            "entity_data", () -> AttachmentType.serializable((holder) -> new LREntityData()).build()
    );

    public static final Supplier<AttachmentType<LRProjectileData>> PROJECTILE_DATA = ATTACHMENT_TYPES.register(
            "projectile_data", () -> AttachmentType.serializable((holder) -> new LRProjectileData()).build()
    );

    public static void register(IEventBus eventBus){
        ATTACHMENT_TYPES.register(eventBus);
    }
}
