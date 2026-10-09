package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.lootCondition.ChanceModifierCondition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 本模组的战利品条件类型。
 */
public class LRLootConditions {

    private static final DeferredRegister<LootItemConditionType> CONDITIONS =
            DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, LegendaryRelics.MODID);

    /** 概率加工链：从 1.0 起算，依次加工后做随机判定。 */
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> CHANCE_MODIFIERS =
            CONDITIONS.register("chance_modifiers", () -> new LootItemConditionType(ChanceModifierCondition.CODEC));

    public static void register(IEventBus modEventBus) {
        CONDITIONS.register(modEventBus);
    }
}
