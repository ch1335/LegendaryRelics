package com.chen1335.equipmentEffectLib.equipmentType;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;
import com.chen1335.equipmentEffectLib.equipmentSources.ArmorSource;
import com.chen1335.equipmentEffectLib.equipmentSources.CuriosSource;
import com.chen1335.equipmentEffectLib.equipmentSources.HandsSource;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class ALLType extends EquipmentType {
    public static final ALLType INSTANCE = new ALLType();
    public static final Set<IEquipmentSource> SOURCES = new HashSet<>();

    protected ALLType() {
        super(livingEntity -> {
            ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
            for (IEquipmentSource value : SOURCES) {
                builder.addAll(value.get(livingEntity));
            }
            return builder.build();
        });
    }

    static {
        SOURCES.add(ArmorSource.INSTANCE);
        SOURCES.add(CuriosSource.INSTANCE);
        SOURCES.add(HandsSource.INSTANCE);
    }

    @Override
    public boolean contain(IEquipmentType equipmentType) {
        return true;
    }
}
