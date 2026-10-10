package com.chen1335.legendaryRelics.common.lootInjecter;

import com.mojang.serialization.MapCodec;

/** 和 LootPoolEntryType / LootItemConditionType 同款：把 MapCodec 包成注册表项，方便别的模组加自己的 op。 */
public record LootInjectorOpType(MapCodec<? extends LootInjectorOp> codec) {
}