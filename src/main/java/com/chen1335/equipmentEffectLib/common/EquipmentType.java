package com.chen1335.equipmentEffectLib.common;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;
import com.chen1335.equipmentEffectLib.equipmentSources.ArmorSource;
import com.chen1335.equipmentEffectLib.equipmentSources.CuriosSource;
import com.chen1335.equipmentEffectLib.equipmentSources.HandSource;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EquipmentType implements IEquipmentType {
    private final ResourceLocation id;
    private final IEquipmentSource source;

    public EquipmentType(ResourceLocation id, IEquipmentSource source) {
        this.id = id;
        this.source = source;
    }

    private static final Map<ResourceLocation, EquipmentType> UNIT_TYPES = new HashMap<>();
    private static final Map<ResourceLocation, EquipmentType> COMBINE_TYPES = new HashMap<>();
    public static final EquipmentType CURIO = getOrRegister("curio", CuriosSource.INSTANCE);

    public static final EquipmentType ARMOR = getOrRegister("armor", ArmorSource.INSTANCE);

    public static final EquipmentType HAND = getOrRegister("hand", HandSource.INSTANCE);

    public static final EquipmentType ALL = getOrRegisterCombine("all", ALLType.INSTANCE);

    public static final EquipmentType ALL_WITHOUT_HAND = getOrRegisterCombine("all_without_hand", ALLWithoutHandType.INSTANCE);

    public static EquipmentType getOrRegister(ResourceLocation resourceLocation, IEquipmentSource source) {
        return UNIT_TYPES.computeIfAbsent(resourceLocation, resourceLocation1 -> new EquipmentType(resourceLocation1, source));
    }

    private static EquipmentType getOrRegister(String id, IEquipmentSource source) {
        return getOrRegister(LegendaryRelics.id(id), source);
    }

    public static EquipmentType getOrRegisterCombine(ResourceLocation resourceLocation, IEquipmentType... types) {
        return UNIT_TYPES.computeIfAbsent(resourceLocation, resourceLocation1 -> new CombineType(resourceLocation1, types));
    }

    private static EquipmentType getOrRegisterCombine(String id, IEquipmentType... types) {
        return getOrRegisterCombine(LegendaryRelics.id(id), types);
    }

    public static Map<ResourceLocation, EquipmentType> getUnits() {
        return UNIT_TYPES;
    }

    @Override
    public IEquipmentSource source() {
        return source;
    }

    public ResourceLocation getId() {
        return id;
    }

    public static class CombineType extends EquipmentType {
        public CombineType(ResourceLocation id, IEquipmentType... types) {
            super(id, livingEntity -> {
                ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
                for (IEquipmentType type : types) {
                    builder.addAll(type.source().get(livingEntity));
                }
                return builder.build();
            });
        }
    }

    public static class ALLType implements IEquipmentType {
        public static final ALLType INSTANCE = new ALLType();

        private final IEquipmentSource source = livingEntity -> {
            ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
            for (EquipmentType value : UNIT_TYPES.values()) {
                builder.addAll(value.source().get(livingEntity));
            }
            return builder.build();
        };

        @Override
        public IEquipmentSource source() {
            return source;
        }
    }

    public static class ALLWithoutHandType implements IEquipmentType {
        public static final ALLType INSTANCE = new ALLType();

        private final IEquipmentSource source = livingEntity -> {
            ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
            for (EquipmentType value : UNIT_TYPES.values()) {
                if (value != EquipmentType.HAND) {
                    builder.addAll(value.source().get(livingEntity));
                }
            }
            return builder.build();
        };

        @Override
        public IEquipmentSource source() {
            return source;
        }
    }
}
