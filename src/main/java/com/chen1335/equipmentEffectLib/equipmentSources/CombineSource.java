package com.chen1335.equipmentEffectLib.equipmentSources;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CombineSource implements IEquipmentSource {
    private final IEquipmentSource[] sources;

    public CombineSource(IEquipmentSource... sources) {
        this.sources = sources;
    }


    @Override
    public List<ItemStack> get(LivingEntity livingEntity) {
        ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        for (IEquipmentSource source : sources) {
            builder.addAll(source.get(livingEntity));
        }
        return builder.build();
    }
}
