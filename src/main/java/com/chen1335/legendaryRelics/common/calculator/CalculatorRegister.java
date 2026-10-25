package com.chen1335.legendaryRelics.common.calculator;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.calculator.api.OperationUnit;
import com.chen1335.legendaryRelics.common.calculator.normal.*;
import com.chen1335.legendaryRelics.common.calculator.special.*;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class CalculatorRegister {
    public static final ResourceKey<Registry<OperationType>> KEY =
            ResourceKey.createRegistryKey(LegendaryRelics.id("calculator_operation"));

    public static final Registry<OperationType> REGISTRY =
            new RegistryBuilder<>(KEY).sync(true).create();

    private static final DeferredRegister<OperationType> OPERATIONS =
            DeferredRegister.create(KEY, LegendaryRelics.MODID);

    public static final DeferredHolder<OperationType, OperationType> ADD =
            OPERATIONS.register("add", () -> new OperationType(Add.CODEC, Add.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> CONSTANT =
            OPERATIONS.register("constant", () -> new OperationType(Constant.CODEC, Constant.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> MUL =
            OPERATIONS.register("mul", () -> new OperationType(Mul.CODEC, Mul.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> MULTI_ADD =
            OPERATIONS.register("multi_add", () -> new OperationType(MultiAdd.CODEC, MultiAdd.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> MULTI_MUL =
            OPERATIONS.register("multi_mul", () -> new OperationType(MultiMul.CODEC, MultiMul.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> DARK_GOLD_UPDATE =
            OPERATIONS.register("dark_gold_update", () -> new OperationType(DarkGoldUpdateArg.CODEC, DarkGoldUpdateArg.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> ENTITY_ATTRIBUTE =
            OPERATIONS.register("entity_attribute", () -> new OperationType(EntityAttributeValue.CODEC, EntityAttributeValue.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> EQUIPMENT_EFFECT_LEVEL =
            OPERATIONS.register("equipment_effect_level", () -> new OperationType(EquipmentEffectLevelArg.CODEC, EquipmentEffectLevelArg.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> SINGLE_CUSTOM =
            OPERATIONS.register("single_custom", () -> new OperationType(SingleCustomArg.CODEC, SingleCustomArg.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> TIERED_BONUS =
            OPERATIONS.register("tiered_bonus", () -> new OperationType(TieredBonus.CODEC, TieredBonus.STREAM_CODEC));
    public static final DeferredHolder<OperationType, OperationType> SPECIAL_EFFECT_STACK =
            OPERATIONS.register("special_effect_stack", () -> new OperationType(SpecialEffectStackArg.CODEC, SpecialEffectStackArg.STREAM_CODEC));

    public static final StreamCodec<RegistryFriendlyByteBuf, OperationType> STREAM_CODEC =
            StreamCodec.composite(ResourceLocation.STREAM_CODEC, REGISTRY::getKey, REGISTRY::get);

    public static final Codec<OperationType> CODEC = REGISTRY.byNameCodec();

    public static final StreamCodec<RegistryFriendlyByteBuf, OperationUnit> DISPATCH_STREAM_CODEC =
            STREAM_CODEC.dispatch(OperationUnit::getType, OperationType::streamCodec);

    public static final Codec<OperationUnit> DISPATCH_CODEC =
            CODEC.dispatch(OperationUnit::getType, OperationType::codec);

    public static void register(IEventBus modEventBus) {
        OPERATIONS.register(modEventBus);
    }

    public static void newRegistry(NewRegistryEvent event) {
        event.register(REGISTRY);
    }
}
