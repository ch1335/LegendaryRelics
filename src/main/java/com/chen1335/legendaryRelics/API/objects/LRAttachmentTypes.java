package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.AttachmentDatas.LREntityData;
import com.chen1335.legendaryRelics.LegendaryRelics;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class LRAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, LegendaryRelics.MODID);

    public static final Supplier<AttachmentType<LREntityData>> ENTITY_DATA = ATTACHMENT_TYPES.register(
            "entity_data", () -> AttachmentType.serializable((holder) -> new LREntityData()).build()
    );
}
