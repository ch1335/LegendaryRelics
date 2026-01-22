package com.chen1335.equipmentEffectLib.kubejs;

import com.chen1335.equipmentEffectLib.API.EquipmentEffectAPI;
import com.chen1335.equipmentEffectLib.API.objects.EERegisterTypes;
import com.chen1335.equipmentEffectLib.EquipmentEffectLib;
import com.chen1335.legendaryRelics.events.AttachItemEffectEvent;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class EquipmentEffectKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.of(EERegisterTypes.EQUIPMENT_EFFECT_TYPE_KEY, reg -> {
            reg.add(EquipmentEffectLib.id("curio"), EquipmentEffectBuilder.CurioEffectBuilder.class, EquipmentEffectBuilder.CurioEffectBuilder::new);
            reg.add(EquipmentEffectLib.id("armor"), EquipmentEffectBuilder.ArmorEffectBuilder.class, EquipmentEffectBuilder.ArmorEffectBuilder::new);
            reg.add(EquipmentEffectLib.id("main_hand"), EquipmentEffectBuilder.MainHandEffectBuilder.class, EquipmentEffectBuilder.MainHandEffectBuilder::new);
        });
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("AttachItemEffectEvent", AttachItemEffectEvent.class);
        bindings.add("EquipmentEffectAPI", EquipmentEffectAPI.class);
    }
}
