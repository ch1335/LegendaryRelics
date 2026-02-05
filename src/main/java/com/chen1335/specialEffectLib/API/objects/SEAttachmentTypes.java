package com.chen1335.specialEffectLib.API.objects;

import com.chen1335.specialEffectLib.SpecialEffectLib;
import com.chen1335.specialEffectLib.attachmentDatas.EntityEffectData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.function.Supplier;

public class SEAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SpecialEffectLib.MODID);

    public static final Supplier<AttachmentType<EntityEffectData>> ENTITY_EFFECT_DATA = ATTACHMENT_TYPES.register(
            "entity_effect_data", () -> AttachmentType.serializable((holder) -> new EntityEffectData(new HashMap<>())).build()
    );
}
