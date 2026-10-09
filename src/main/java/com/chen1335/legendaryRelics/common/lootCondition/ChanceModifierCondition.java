package com.chen1335.legendaryRelics.common.lootCondition;

import com.chen1335.legendaryRelics.API.objects.LRLootConditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.List;

/**
 * 从 1.0 起算，按顺序过一遍所有 {@link ChanceModifier}，再用最终概率做一次随机判定。
 *
 * <p>概率不夹紧：加工出大于 1 的值就是必定通过，小于等于 0 就是必定不通过。
 */
public record ChanceModifierCondition(List<ChanceModifier> modifiers) implements LootItemCondition {

    public static final MapCodec<ChanceModifierCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ChanceModifier.CODEC.listOf().fieldOf("chance_modifiers").forGetter(ChanceModifierCondition::modifiers)
    ).apply(instance, ChanceModifierCondition::new));

    public static LootItemCondition.Builder builder(ChanceModifier... modifiers) {
        return () -> new ChanceModifierCondition(List.of(modifiers));
    }

    @Override
    public LootItemConditionType getType() {
        return LRLootConditions.CHANCE_MODIFIERS.get();
    }

    @Override
    public boolean test(LootContext context) {
        double chance = 1.0D;

        for (ChanceModifier modifier : this.modifiers) {
            chance = modifier.apply(chance, context);
        }

        return context.getRandom().nextFloat() < chance;
    }
}
