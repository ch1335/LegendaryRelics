package com.chen1335.legendaryRelics.data.lootTables;

import com.chen1335.arcanumlib.modules.loot.lootCondition.ChanceModifier;
import com.chen1335.arcanumlib.modules.loot.lootCondition.ChanceModifierCondition;
import com.chen1335.legendaryRelics.API.objects.LREntityTypes;
import com.chen1335.legendaryRelics.API.objects.LRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * 实体战利品表。
 *
 * <p>冰川骷髅：骨头沿用原版骷髅（0~2，每级抢夺再 +0~1）；冰 0~1，每级抢夺再 +0~1；
 * 霜锥单独一个池，基础 5%，抢夺每级 +0.5%，攻击者幸运每点 +0.5%，最后按难度 ×1/×1/×1.15/×1.3。
 * 手持那把永不掉落（见 {@code GlaciateSkeleton#finalizeSpawn}），所以霜锥只能从这里拿到。
 */
public class LREntityLootTables extends EntityLootSubProvider {

    protected LREntityLootTables(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        this.add(LREntityTypes.GLACIATE_SKELETON.get(), LootTable.lootTable()
                // 骨头：数值与原版骷髅一致
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.BONE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
                // 冰：0~1，每级抢夺再 +0~1（期望上等同抬高上限）
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.ICE)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
                // 霜锥：基础 5% → 抢夺每级 +0.5% → 攻击者幸运每点 +0.5% → 按难度缩放
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(LRItems.FROST_SHARD.get())
                                .when(ChanceModifierCondition.builder(0.05D,
                                        ChanceModifier.ToolEnchantmentLevel.of(Enchantments.LOOTING,
                                                ChanceModifier.Operation.ADD, 0.005D),
                                        ChanceModifier.Luck.of(
                                                ChanceModifier.Operation.ADD, 0.005D),
                                        ChanceModifier.DifficultyFactor.multiply(1.0D, 1.0D, 1.15D, 1.3D))))));
    }

    /** 只登记本模组的实体，否则会因原版实体缺表而报错。 */
    @Override
    protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
        return Stream.of(LREntityTypes.GLACIATE_SKELETON.get());
    }
}
