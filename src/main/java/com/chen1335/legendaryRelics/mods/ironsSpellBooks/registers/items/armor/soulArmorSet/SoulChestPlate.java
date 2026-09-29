package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.items.armor.soulArmorSet;

import io.redspace.ironsspellbooks.api.spells.IPresetSpellContainer;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import net.minecraft.world.item.ItemStack;

public class SoulChestPlate extends SoulArmor implements IPresetSpellContainer {

    public SoulChestPlate() {
        super(Type.CHESTPLATE, new Properties());
    }

    /**
     * 灵魂胸甲自带一个法术注入槽位。
     *
     * <p>已有容器时不做覆盖，避免抹掉已注入的法术。
     */
    @Override
    public void initializeSpellContainer(ItemStack itemStack) {
        if (!ISpellContainer.isSpellContainer(itemStack)) {
            ISpellContainer.set(itemStack, ISpellContainer.create(1, true, true));
        }
    }
}
