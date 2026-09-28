package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.weapons;

import com.chen1335.equipmentEffectLib.API.IEffectEquipment;
import com.chen1335.equipmentEffectLib.API.objects.EquipmentTypes;
import com.chen1335.equipmentEffectLib.effectBase.BaseEffect;
import com.chen1335.legendaryRelics.API.ILRItemExtension;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSTiers;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.world.item.Rarity;

import java.util.List;

/**
 * 破魔：命中后抑制目标的法术输出，并自带一级反制法术。
 */
public class MagicBreaker extends MagicSwordItem implements IEffectEquipment, ILRItemExtension {

    public MagicBreaker() {
        super(ISSTiers.Weapon.MAGIC_BREAKER, new Properties()
                        .stacksTo(1)
                        .rarity(Rarity.EPIC)
                        .fireResistant()
                        .attributes(ExtendedSwordItem.createAttributes(ISSTiers.Weapon.MAGIC_BREAKER)),
                SpellDataRegistryHolder.of(new SpellDataRegistryHolder(SpellRegistry.COUNTERSPELL_SPELL, 1)));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(ISSRegisters.EquipmentEffect.MAGIC_SUPPRESSION.value().create(1, EquipmentTypes.MAIN_HAND));
    }
}
