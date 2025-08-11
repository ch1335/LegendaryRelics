package com.chen1335.equipmentEffectLib.API.objects;

import com.chen1335.equipmentEffectLib.EquipmentEffectLib;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntityEquipmentEffectData;
import com.chen1335.equipmentEffectLib.attachmentDatas.EntitySetsEffectData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EEAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EquipmentEffectLib.MODID);

    public static final Supplier<AttachmentType<EntityEquipmentEffectData>> ENTITY_EQUIPMENT_EFFECT_DATA = ATTACHMENT_TYPES.register(
            "entity_equipment_effect_data", () -> AttachmentType.builder((holder) -> new EntityEquipmentEffectData()).build()
    );

    public static final Supplier<AttachmentType<EntitySetsEffectData>> ENTITY_SETS_EFFECT_DATA = ATTACHMENT_TYPES.register(
            "entity_sets_effect_data", () -> AttachmentType.builder((holder) -> new EntitySetsEffectData()).build()
    );
}
