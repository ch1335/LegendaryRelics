package com.chen1335.legendaryRelics.API.objects;

import com.chen1335.legendaryRelics.LegendaryRelics;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.BooleanAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 本模组的自定义属性。
 */
public class LRAttributes {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, LegendaryRelics.MODID);

    /**
     * 细雪行走：值大于 0 时，实体可以站在细雪上，也不会因细雪而冻结。基础值 0，由雪羽斗篷以 {@code ADD_VALUE(+1)} 提供。
     *
     * <p>属性名保持通用 —— 它描述的是"能否在细雪上行走"这件事本身，任何来源都可以给它加值，
     * 不必和某个具体饰品绑在一起。
     *
     * <p>为什么做成属性而不是直接查饰品：
     * 细雪判定在客户端也会跑（<b>本地玩家移动预测</b>），如果客户端查不到饰品就会按"要下沉"来预测，
     * 服务端却按"站在上面"结算，两边来回纠正 —— 表现为站在细雪上抽搐。
     * 属性只要 {@code setSyncable(true)} 就会被原版同步给客户端（{@code AttributeMap#getSyncableAttributes}
     * 按 {@code isClientSyncable()} 过滤），两端读到的值一致，问题自然消失。
     * 这也是 NeoForge 自己 {@code NeoForgeMod.CREATIVE_FLIGHT} 的做法。
     *
     * <p>{@code Neutral} 情绪：避免被模组里"提升大部分属性"的效果（只处理 POSITIVE）顺手加上 modifier。
     */
    public static final DeferredHolder<Attribute, Attribute> POWDER_SNOW_WALKING = ATTRIBUTES.register(
            "powder_snow_walking",
            () -> new BooleanAttribute("attribute.name.legendary_relics.powder_snow_walking", false)
                    .setSyncable(true)
                    .setSentiment(Attribute.Sentiment.NEUTRAL));

    public static void register(IEventBus modEventBus) {
        ATTRIBUTES.register(modEventBus);
    }
}
