package com.chen1335.legendaryRelics.common.lootInjecter.datagen;

import com.chen1335.legendaryRelics.common.lootInjecter.LootInjector;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 战利品注入器的数据生成基类，其他模组可以直接继承来生成自己的注入器 json。
 *
 * <p>子类只需要给出注入器清单；编码、落盘、注册表细节都在这里。
 * 用 Codec 编码，所以顺带就是一次 round-trip 验证。
 *
 * <p>条件和函数里有注册表引用，必须用 RegistryOps。另外 EnchantedCountIncreaseFunction 会把附魔
 * 解析成 Holder，而附魔是数据包注册表、datagen 阶段查不到，带它的注入器编码不出来，
 * 那种条目得手写到 src/main/resources 下。
 */
public abstract class LootInjectorProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final String name;

    /** @param directory 数据包目录名，生成到 {@code data/<ns>/<directory>/<id>.json} */
    protected LootInjectorProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                   String directory, String name) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, directory);
        this.registries = registries;
        this.name = name;
    }

    /** 要生成的全部注入器。 */
    protected abstract List<LootInjector> injectors(HolderLookup.Provider registries);

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.registries.thenCompose(provider -> {
            // 必须用 createSerializationContext：它会把 provider 的全部注册表登记进当前注册表集，
            // 否则编码 Holder（例如原版 enchanted_count_increase 的附魔）会因校验不到而失败
            RegistryOps<JsonElement> ops = provider.createSerializationContext(JsonOps.INSTANCE);
            List<CompletableFuture<?>> futures = new ArrayList<>();

            for (LootInjector injector : injectors(provider)) {
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
        return this.name;
    }
}