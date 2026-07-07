package com.chen1335.equipmentEffectLib.equipmentType;

import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class CombineType extends EquipmentType {
    private final Set<EquipmentType> types;

    public CombineType(EquipmentType... types) {
        super(livingEntity -> {
            ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
            for (EquipmentType type : types) {
                builder.addAll(type.source().get(livingEntity));
            }
            return builder.build();
        });
        this.types = Set.of(types);
    }

    @Override
    public boolean match(IEquipmentType equipmentType) {
        for (IEquipmentType type : types) {
            if (type.match(equipmentType)) {
                return true;
            }
        }
        return false;
    }
}
