package com.chen1335.equipmentEffectLib.common;

import com.chen1335.equipmentEffectLib.API.IEquipmentSource;
import com.chen1335.equipmentEffectLib.API.objects.IEquipmentType;
import com.chen1335.equipmentEffectLib.equipmentSources.ArmorSource;
import com.chen1335.equipmentEffectLib.equipmentSources.CuriosSource;
import com.chen1335.equipmentEffectLib.equipmentSources.HandsSource;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EquipmentType implements IEquipmentType {
    private static final BiMap<ResourceLocation, EquipmentType> ALL_TYPES = HashBiMap.create();
    private static final Map<ResourceLocation, EquipmentType> UNIT_TYPES = new HashMap<>();
    private static final Map<ResourceLocation, EquipmentType> COMBINE_TYPES = new HashMap<>();

    public static final Codec<EquipmentType> CODEC = ResourceLocation.CODEC.xmap(ALL_TYPES::get, equipmentType -> ALL_TYPES.inverse().get(equipmentType));

    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentType> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            equipmentType -> ALL_TYPES.inverse().get(equipmentType),
            ALL_TYPES::get
    );
    private final ResourceLocation id;
    private final IEquipmentSource source;

    public EquipmentType(ResourceLocation id, IEquipmentSource source) {
        this.id = id;
        this.source = source;
    }

    public static final EquipmentType CURIO = getOrRegister("curio", CuriosSource.INSTANCE);

    public static final EquipmentType ARMOR = getOrRegister("armor", ArmorSource.INSTANCE);

    public static final EquipmentType HANDS = getOrRegister("hands", HandsSource.INSTANCE);

    public static final EquipmentType ARMOR_AND_HANDS = getOrRegisterCombine("armor_and_hands", ARMOR, HANDS);

    public static final EquipmentType ALL = getOrRegisterCombine("all", ALLType.INSTANCE);

    public static final EquipmentType NON = getOrRegisterCombine("non", new IEquipmentType() {
        @Override
        public IEquipmentSource source() {
            return livingEntity -> List.of();
        }

        @Override
        public boolean match(IEquipmentType equipmentType) {
            return false;
        }
    });

    public static EquipmentType getOrRegister(ResourceLocation resourceLocation, IEquipmentSource source) {
        return UNIT_TYPES.computeIfAbsent(resourceLocation, resourceLocation1 -> {
            EquipmentType equipmentType = new EquipmentType(resourceLocation1, source);
            ALL_TYPES.put(resourceLocation1, equipmentType);
            return equipmentType;
        });
    }

    private static EquipmentType getOrRegister(String id, IEquipmentSource source) {
        return getOrRegister(LegendaryRelics.id(id), source);
    }

    public static EquipmentType getOrRegisterCombine(ResourceLocation resourceLocation, IEquipmentType... types) {
        return COMBINE_TYPES.computeIfAbsent(resourceLocation, resourceLocation1 -> {
            CombineType combineType = new CombineType(resourceLocation1, types);
            ALL_TYPES.put(resourceLocation1, combineType);
            return combineType;
        });
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

    @Override
    public boolean match(IEquipmentType equipmentType) {
        return equipmentType == this;
    }

    public static class CombineType extends EquipmentType {
        private final Set<IEquipmentType> types;

        public CombineType(ResourceLocation id, IEquipmentType... types) {
            super(id, livingEntity -> {
                ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
                for (IEquipmentType type : types) {
                    builder.addAll(type.source().get(livingEntity));
                }
                return builder.build();
            });
            this.types = Set.of(types);
        }

        @Override
        public boolean match(IEquipmentType equipmentType) {
            return types.contains(equipmentType);
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

        @Override
        public boolean match(IEquipmentType equipmentType) {
            return true;
        }
    }

    public static class ALLWithoutHandType implements IEquipmentType {
        public static final ALLType INSTANCE = new ALLType();

        private final IEquipmentSource source = livingEntity -> {
            ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
            for (EquipmentType value : UNIT_TYPES.values()) {
                if (value != EquipmentType.HANDS) {
                    builder.addAll(value.source().get(livingEntity));
                }
            }
            return builder.build();
        };

        @Override
        public IEquipmentSource source() {
            return source;
        }

        @Override
        public boolean match(IEquipmentType equipmentType) {
            return !(equipmentType == EquipmentType.HANDS);
        }
    }
}
