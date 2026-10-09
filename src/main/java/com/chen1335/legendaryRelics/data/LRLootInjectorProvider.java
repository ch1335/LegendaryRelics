package com.chen1335.legendaryRelics.data;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.common.lootInjecter.LootInjector;
import com.chen1335.legendaryRelics.common.lootInjecter.LootInjectorOp;
import com.chen1335.legendaryRelics.common.lootInjecter.condition.EntryCondition;
import com.chen1335.legendaryRelics.common.lootInjecter.condition.PoolCondition;
import com.chen1335.legendaryRelics.common.lootInjecter.condition.TableCondition;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 生成模组【内置】的战利品注入器。
 *
 * <p>用 Codec 编码，所以顺带就是一次 round-trip 验证：哪个字段的 Codec 有问题，runData 会直接报错。
 * 条件/函数里有注册表引用，必须用 RegistryOps，纯 JsonOps 会找不到注册项。
 *
 * <p>要玩家或整合包手改的注入器请放 src/main/resources 下，那边不会被 runData 覆盖。
 */
public class LRLootInjectorProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public LRLootInjectorProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_injecter");
        this.registries = registries;
    }

    /**
     * 内置注入器清单。
     *
     * <p>现在放的是测试用那条：正好覆盖四种操作，方便验证生成结果和实际效果。
     * 发布前要把 test 这条删掉，否则玩家会白得钻石、绿宝石，并且掉不出冰和霜锥。
     */
    private static List<LootInjector> builtin() {
        return List.of(new LootInjector(
                LegendaryRelics.id("test"),
                new TableCondition.ById(LegendaryRelics.id("entities/glacite_skeleton")),
                1000,
                List.of(
                        new LootInjectorOp.AddPool(LootPool.lootPool()
                                .name("injector_test")
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(Items.DIAMOND))
                                .build()),
                        new LootInjectorOp.AddEntry(
                                new PoolCondition.ByIndex(0),
                                LootItem.lootTableItem(Items.EMERALD).build()),
                        new LootInjectorOp.RemoveEntry(
                                new PoolCondition.ByIndex(1),
                                new EntryCondition.IsItem(ResourceLocation.withDefaultNamespace("ice"))),
                        new LootInjectorOp.ModifyEntry(
                                new PoolCondition.ByIndex(2),
                                new EntryCondition.IsItem(LegendaryRelics.id("frost_shard")),
                                List.of(LootItemRandomChanceCondition.randomChance(0.0F).build()),
                                List.of(),
                                -1,
                                -1))));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.registries.thenCompose(provider -> {
            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, provider);
            List<CompletableFuture<?>> futures = new ArrayList<>();

            for (LootInjector injector : builtin()) {
                // id 不是 Codec 的字段，由文件路径决定，所以编码时不用管它
                JsonElement json = LootInjector.CODEC.encodeStart(ops, injector)
                        .getOrThrow(error -> new IllegalStateException(
                                "Failed to encode loot injector " + injector.id() + ": " + error));

                futures.add(DataProvider.saveStable(cache, json, this.pathProvider.json(injector.id())));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Loot Injectors";
    }
}