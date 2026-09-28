package com.chen1335.legendaryRelics.mods.ironsSpellBooks;

import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.events.SetItemSetsEffectEvent;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.armorSetEffect.SoulArmorSetEffect;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.specialMobEffects.MagicSuppression;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Map;

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

    /**
     * 法术抑制：施法者身上带有该效果时，按比例削减其造成的法术伤害。
     */
    public static void spellDamage(SpellDamageEvent event) {
        if (!(event.getSpellDamageSource().getEntity() instanceof LivingEntity caster)) {
            return;
        }
        float reduction = getMagicSuppressionReduction(caster);
        if (reduction > 0.0F) {
            event.setAmount(event.getAmount() * (1.0F - reduction));
        }
    }

    /**
     * 取实体身上最强的法术抑制减伤比例；没有该效果时返回 0。
     */
    private static float getMagicSuppressionReduction(LivingEntity entity) {
        float reduction = 0.0F;
        for (Map<MobEffectType<?>, SpecialMobEffect> effects : SpecialEffectAPI.getEntityEffectData(entity).getEffects().values()) {
            if (effects.get(ISSRegisters.SpecialMobEffect.MAGIC_SUPPRESSION.get()) instanceof MagicSuppression suppression) {
                reduction = Math.max(reduction, suppression.getDamageReduction());
            }
        }
        return reduction;
    }
}
