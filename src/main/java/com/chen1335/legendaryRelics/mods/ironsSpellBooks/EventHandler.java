package com.chen1335.legendaryRelics.mods.ironsSpellBooks;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.events.SetItemSetsEffectEvent;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.armorSetEffect.SoulArmorSetEffect;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class EventHandler {

    public static void setItemSetsEffect(SetItemSetsEffectEvent event) {
        event.sets(ISSRegisters.ArmorSetEffect.SOUL_ARMOR.value(), EquipmentTypes.HUMANOID_ARMOR,
                ISSRegisters.Item.SOUL_HELMET.asItem(),
                ISSRegisters.Item.SOUL_CHEST_PLATE.asItem(),
                ISSRegisters.Item.SOUL_LEGGINGS.asItem(),
                ISSRegisters.Item.SOUL_BOOTS.asItem()
        );
    }

    public static void livingIncomingDamage(LivingIncomingDamageEvent event) {
        SoulArmorSetEffect.LivingIncomingDamageEvent(event);
    }
}
