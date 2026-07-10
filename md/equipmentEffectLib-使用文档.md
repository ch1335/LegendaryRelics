# equipmentEffectLib 使用文档

> 本文档基于项目当前源码编写，适用于当前分支中的 `com.chen1335.equipmentEffectLib` 实现。
>
> 框架主要面向 Minecraft 1.21.1 + NeoForge，用于给原版装备、Curios 物品和自定义装备来源附加可序列化的装备效果及套装效果。

## 目录

- [1. 核心概念](#1-核心概念)
- [2. 初始化](#2-初始化)
- [3. 注册装备效果类型](#3-注册装备效果类型)
- [4. 编写装备效果](#4-编写装备效果)
- [5. 给物品附加默认效果](#5-给物品附加默认效果)
- [6. 通过事件给物品附加效果](#6-通过事件给物品附加效果)
- [7. 查询实体当前装备效果](#7-查询实体当前装备效果)
- [8. 查询物品上的效果](#8-查询物品上的效果)
- [9. 可叠加效果](#9-可叠加效果)
- [10. 生命周期与最佳效果](#10-生命周期与最佳效果)
- [11. Tick 效果](#11-tick-效果)
- [12. Tooltip](#12-tooltip)
- [13. 属性修改器与槽位上下文](#13-属性修改器与槽位上下文)
- [14. 效果自定义数据与序列化](#14-效果自定义数据与序列化)
- [15. 动态修改 ItemStack 效果](#15-动态修改-itemstack-效果)
- [16. 子效果](#16-子效果)
- [17. 装备类型与装备来源](#17-装备类型与装备来源)
- [18. 套装效果](#18-套装效果)
- [19. 事件接口](#19-事件接口)
- [20. 数据组件和实体附件](#20-数据组件和实体附件)
- [21. 完整最小示例](#21-完整最小示例)
- [22. 当前实现注意事项](#22-当前实现注意事项)

---

## 1. 核心概念

### 1.1 `EffectType<T>`

`EffectType<T extends BaseEffect>` 是装备效果的注册类型和工厂。每一种效果都应注册一个独立的 `EffectType`。

它负责：

- 创建具体的 `BaseEffect` 实例；
- 标记效果是否允许叠加；
- 根据类型查询实体当前效果。

### 1.2 `BaseEffect`

`BaseEffect` 是所有装备效果的基础类，保存：

- 效果类型 `EffectType<?>`；
- 效果等级；
- 适用的 `EquipmentType`；
- 效果自己的 Data Component 数据；
- 可选的简单 NBT 数据。

### 1.3 `EquipmentType`

`EquipmentType` 描述效果适用于哪些装备位置，例如：

- 头盔；
- 胸甲；
- 护腿；
- 鞋子；
- 主手；
- 副手；
- Curios；
- 多种位置的组合。

### 1.4 `SlotEffectHolder<T>`

实体上的效果查询不会只返回效果对象，而是返回：

```java
SlotEffectHolder<T>
```

它包含：

```java
holder.context();                  // 效果来自哪个槽位
holder.infoHolder().itemStack();   // 效果来源物品
holder.infoHolder().effect();      // 效果实例
```

这是当前 API 中最重要的查询结果类型。

### 1.5 `SetEffect`

`SetEffect` 是独立于 `BaseEffect` 的套装系统。它按同一套装当前装备件数创建和更新 `EffectInstance`。

---

## 2. 初始化

在 Mod 构造方法中调用：

```java
public MyMod(IEventBus modEventBus, ModContainer modContainer) {
    EquipmentEffectLib.init(modEventBus, modContainer);

    MyEquipmentEffectTypes.register(modEventBus);
    MySetEffects.register(modEventBus);
}
```

`EquipmentEffectLib.init(...)` 当前会注册：

- 物品效果数据组件；
- 实体装备效果附件；
- 实体套装效果附件；
- 内置装备类型；
- `ModifyDefaultComponentsEvent` 监听器。

框架使用三个同步自定义注册表：

| 注册表 | 字段 |
|---|---|
| 装备效果类型 | `EERegisterTypes.EQUIPMENT_EFFECT_TYPE` |
| 套装效果 | `EERegisterTypes.SETS_EFFECT_TYPE` |
| 装备类型 | `EERegisterTypes.EQUIPMENT_TYPE` |

---

## 3. 注册装备效果类型

### 3.1 创建 DeferredRegister

```java
public final class MyEquipmentEffectTypes {
    private static final DeferredRegister<EffectType<?>> EFFECT_TYPES =
            DeferredRegister.create(EERegisterTypes.EQUIPMENT_EFFECT_TYPE, MyMod.MODID);

    private static <T extends BaseEffect> DeferredHolder<EffectType<?>, EffectType<T>> register(
            String name,
            Supplier<EffectType<T>> factory
    ) {
        return EFFECT_TYPES.register(name, factory);
    }

    public static void register(IEventBus eventBus) {
        EFFECT_TYPES.register(eventBus);
    }
}
```

### 3.2 注册不可叠加效果

```java
public static final DeferredHolder<EffectType<?>, EffectType<MyEffect>> MY_EFFECT =
        register("my_effect", () -> new EffectType<>(MyEffect::new));
```

`MyEffect::new` 对应的构造函数必须满足：

```java
MyEffect(EffectType<?> effectType, int level, EquipmentType equipmentType)
```

### 3.3 注册可叠加效果

```java
public static final DeferredHolder<EffectType<?>, EffectType<MyStackableEffect>> MY_STACKABLE_EFFECT =
        register("my_stackable_effect", () -> new EffectType<>(MyStackableEffect::new, true));
```

第二个参数 `true` 会令：

```java
effectType.isStackable() == true
```

注意：这个字段目前主要作为效果类型的语义标记。`findStackableEffect` 本身不会验证该字段，调用者应保证查询方式与注册语义一致。

---

## 4. 编写装备效果

### 4.1 最基础的效果类

```java
public class MyEffect extends BaseEffect {
    public MyEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }
}
```

### 4.2 获取效果等级

```java
int rawLevel = getRawEffectLevel();
```

设置原始等级：

```java
setRawEffectLevel(3);
```

如果等级需要根据佩戴者或物品动态变化，可以覆写：

```java
@Override
public int getEffectLevel(@Nullable LivingEntity living, ItemStack itemStack) {
    return getRawEffectLevel();
}
```

实体效果管理器会使用：

```java
effect.getEffectLevel(living, itemStack)
```

作为同类型效果的等级排序键。

### 4.3 激活和失活

```java
@Override
public void onActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
    // 当前效果成为同类型效果中的最佳效果时调用
}

@Override
public void onDeActive(ISlotContext slotContext, LivingEntity entity, ItemStack itemStack) {
    // 当前效果不再是最佳效果或装备被卸下时调用
}
```

同类型不可叠加效果中，框架会根据 `getEffectLevel(...)` 选择最高等级的效果作为激活效果。

### 4.4 自定义比较逻辑

默认比较逻辑是比较效果等级：

```java
@Override
public boolean isBetterThan(
        LivingEntity entity,
        ItemStack thisItemStack,
        BaseEffect otherEffect,
        ItemStack otherStack
) {
    return getEffectLevel(entity, thisItemStack)
            > otherEffect.getEffectLevel(entity, otherStack);
}
```

当前实体槽位管理的实际排序主要使用 `getEffectLevel(...)`。覆写 `isBetterThan(...)` 前，应确认调用路径是否会使用它。

---

## 5. 给物品附加默认效果

让物品实现 `IEffectEquipment`：

```java
public class MyCurioItem extends Item implements IEffectEquipment {
    public MyCurioItem(Properties properties) {
        super(properties);
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(
                MyEquipmentEffectTypes.MY_EFFECT.value()
                        .create(1, EquipmentTypes.CURIO)
        );
    }
}
```

附加多个效果：

```java
@Override
public List<BaseEffect> getDefaultEffects() {
    return List.of(
            MyEquipmentEffectTypes.MY_EFFECT.value()
                    .create(1, EquipmentTypes.CURIO),
            MyEquipmentEffectTypes.MY_STACKABLE_EFFECT.value()
                    .create(2, EquipmentTypes.CURIO)
    );
}
```

在 `ModifyDefaultComponentsEvent` 中，框架会把这些效果写入物品的默认：

```java
EEItemDataComponentTypes.ITEM_EFFECT
```

数据组件。

---

## 6. 通过事件给物品附加效果

对于无法实现 `IEffectEquipment` 的物品，可以监听 `AttachItemEffectEvent`。

```java
@EventBusSubscriber(modid = MyMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class MyEffectEvents {
    @SubscribeEvent
    public static void attachEffects(AttachItemEffectEvent event) {
        event.addEffect(
                Items.DIAMOND_SWORD,
                MyEquipmentEffectTypes.MY_EFFECT.value(),
                1,
                EquipmentTypes.MAIN_HAND
        );
    }
}
```

也可以直接传入已创建的效果：

```java
event.addEffect(
        Items.DIAMOND_SWORD,
        MyEquipmentEffectTypes.MY_EFFECT.value()
                .create(1, EquipmentTypes.MAIN_HAND)
);
```

同一物品的效果以 `EffectType<?>` 为键，因此同一种效果类型最多保留一个实例，后写入的实例会替换先前实例。

---

## 7. 查询实体当前装备效果

### 7.1 查询最佳效果

```java
EquipmentEffectAPI.findBestEffect(
        livingEntity,
        MyEquipmentEffectTypes.MY_EFFECT.value()
).ifPresent(holder -> {
    MyEffect effect = holder.infoHolder().effect();
    ItemStack sourceStack = holder.infoHolder().itemStack();
    ISlotContext context = holder.context();
});
```

返回类型：

```java
Optional<SlotEffectHolder<MyEffect>>
```

也可以通过效果类型直接调用：

```java
MyEquipmentEffectTypes.MY_EFFECT.value()
        .findBestEffect(livingEntity)
        .ifPresent(holder -> {
            MyEffect effect = holder.infoHolder().effect();
        });
```

### 7.2 查询所有可叠加效果

```java
EquipmentEffectAPI.findStackableEffect(
        livingEntity,
        MyEquipmentEffectTypes.MY_STACKABLE_EFFECT.value()
).ifPresent(holders -> {
    for (SlotEffectHolder<MyStackableEffect> holder : holders) {
        MyStackableEffect effect = holder.infoHolder().effect();
        ItemStack stack = holder.infoHolder().itemStack();
        ISlotContext context = holder.context();
    }
});
```

返回类型：

```java
Optional<List<SlotEffectHolder<MyStackableEffect>>>
```

### 7.3 `SlotEffectHolder` 的结构

```java
public record SlotEffectHolder<T extends BaseEffect>(
        ISlotContext context,
        InfoHolder<T> infoHolder
) {}
```

`InfoHolder` 的结构：

```java
public record InfoHolder<T extends BaseEffect>(
        ItemStack itemStack,
        T effect
) {}
```

---

## 8. 查询物品上的效果

### 8.1 查询指定效果

返回 `Optional<T>`：

```java
Optional<MyEffect> effect = EquipmentEffectAPI.findItemEffect(
        itemStack,
        MyEquipmentEffectTypes.MY_EFFECT.value()
);
```

可空版本：

```java
@Nullable MyEffect effect = EquipmentEffectAPI.getEffect(
        itemStack,
        MyEquipmentEffectTypes.MY_EFFECT.value()
);
```

### 8.2 查询物品全部效果

```java
Map<EffectType<?>, BaseEffect> effects =
        EquipmentEffectAPI.getEffects(itemStack);
```

指定装备类型过滤：

```java
Map<EffectType<?>, BaseEffect> curioEffects =
        EquipmentEffectAPI.getEffects(itemStack, EquipmentTypes.CURIO);
```

`getEffects(...)` 会合并：

1. `ITEM_EFFECT`；
2. `ITEM_EFFECT_ADDITION`；
3. `ISubEffectProvider` 提供的子效果。

返回的是不可修改的 Map。

无参 `getEffects(itemStack)` 使用 `EquipmentTypes.ALL` 过滤。由于当前 `ALLType.contain(...)` 总是返回 `true`，它会返回物品上任意装备类型的效果，包括自定义 `EquipmentType` 的效果。

### 8.3 判断物品是否带有效果数据

```java
boolean hasEffects = EquipmentEffectAPI.haveEffects(itemStack);
```

---

## 9. 可叠加效果

可叠加效果通常由事件处理器主动收集并累加所有来源。

以下是治疗加成的典型结构：

```java
public static void onLivingHeal(LivingHealEvent event) {
    LivingEntity living = event.getEntity();

    EquipmentEffectAPI.findStackableEffect(
            living,
            MyEquipmentEffectTypes.HEAL_BOOST.value()
    ).ifPresent(holders -> {
        float totalBonus = 0.0F;

        for (SlotEffectHolder<HealBoostEffect> holder : holders) {
            HealBoostEffect effect = holder.infoHolder().effect();
            ItemStack sourceStack = holder.infoHolder().itemStack();
            totalBonus += effect.getBonus(living, sourceStack);
        }

        event.setAmount(event.getAmount() * (1.0F + totalBonus));
    });
}
```

不要把 `findStackableEffect` 的列表元素当作 `InfoHolder<T>`；当前元素类型是：

```java
SlotEffectHolder<T>
```

---

## 10. 生命周期与最佳效果

实体装备变化时，框架会：

1. 从旧槽位移除对应的 `SlotEffectHolder`；
2. 为新物品创建新的 `SlotEffectHolder`；
3. 以 `effect.getEffectLevel(living, itemStack)` 为等级键保存；
4. 重新选择同类型的最佳效果；
5. 在最佳效果变化时调用旧效果的 `onDeActive` 和新效果的 `onActive`。

同类型效果可能同时存在多个来源，但只有最佳效果会走激活/失活生命周期。

可叠加效果如果需要所有来源同时生效，通常应在相应游戏事件中使用 `findStackableEffect` 主动聚合，而不是只依赖 `onActive`。

---

## 11. Tick 效果

让效果实现 `ITickAbleEffect`：

```java
public class RegenerationEffect extends BaseEffect implements ITickAbleEffect {
    public RegenerationEffect(
            EffectType<?> effectType,
            int level,
            EquipmentType equipmentType
    ) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void effectTick(ItemStack itemStack, LivingEntity wearer) {
        if (wearer.tickCount % 20 == 0) {
            wearer.heal(getRawEffectLevel());
        }
    }
}
```

框架会在 `EntityTickEvent.Pre` 中遍历实体当前记录的所有装备效果，并调用：

```java
tickAbleEffect.effectTick(itemStack, livingEntity);
```

注意：Tick 遍历的是当前所有 `SlotEffectHolder`，不是只遍历最佳效果。因此同类型效果存在多个来源时，每个实现了 `ITickAbleEffect` 的实例都会收到 Tick。

---

## 12. Tooltip

覆写 `BaseEffect.appendToolTip(...)`：

```java
@Override
public void appendToolTip(
        ItemStack itemStack,
        Item.TooltipContext context,
        Player player,
        TooltipFlag tooltipFlag,
        List<Component> tooltipComponents
) {
    tooltipComponents.add(
            Component.literal("效果等级：" + getRawEffectLevel())
    );
}
```

框架的 `ItemStackMixin` 会读取物品效果并自动调用这个方法。

套装效果则覆写：

```java
SetEffect.appendToolTip(...)
```

---

## 13. 属性修改器与槽位上下文

### 13.1 为什么要使用 `ISlotContext.pathRL`

多个槽位可能装备相同效果。如果所有属性修改器使用相同 ID，它们可能互相覆盖。

下面的 `onActive/onDeActive` 模式适合不可叠加、只允许最佳来源生效的效果。即使 `EffectType` 标记为 stackable，当前生命周期仍只激活同类型中的最佳 holder；若希望每个来源都贡献属性，应在属性事件或自定义聚合逻辑中通过 `findStackableEffect(...)` 统一计算。

建议根据槽位生成唯一 ID：

```java
ResourceLocation baseId = ResourceLocation.fromNamespaceAndPath(
        MyMod.MODID,
        "my_effect_modifier"
);

ResourceLocation slotModifierId = slotContext.pathRL(baseId);
```

然后在 `onActive` 中添加属性修改器：

```java
@Override
public void onActive(ISlotContext context, LivingEntity entity, ItemStack stack) {
    AttributeInstance attribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
    if (attribute == null) return;

    ResourceLocation id = context.pathRL(
            ResourceLocation.fromNamespaceAndPath(MyMod.MODID, "my_effect_damage")
    );

    attribute.removeModifier(id);
    attribute.addTransientModifier(new AttributeModifier(
            id,
            0.1D * getEffectLevel(entity, stack),
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
    ));
}
```

在 `onDeActive` 中用相同 ID 移除：

```java
@Override
public void onDeActive(ISlotContext context, LivingEntity entity, ItemStack stack) {
    AttributeInstance attribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
    if (attribute != null) {
        attribute.removeModifier(context.pathRL(
                ResourceLocation.fromNamespaceAndPath(MyMod.MODID, "my_effect_damage")
        ));
    }
}
```

### 13.2 `ISlotContext` 提供的能力

```java
context.getManagerClass();
context.pathRL(resourceLocation);
context.match(equipmentType);
```

框架内置上下文包括：

- `EquipmentSlotEffectManager.SlotContext`；
- `CurioSlotEffectManager.SlotContext`。

当前 `SlotEffectManager.onChanged(...)` 主要维护 `EntityEquipmentEffectData` 内的 holder 集合。各 manager 的 `getEffectsBySlot(...)` 后备 Map 在现有基类更新流程中没有被同步写入，因此 `EntityEquipmentEffectData.findEffectsBySlot(...)` 在当前实现下不应视为可靠的公共查询 API，除非对应 manager 另外维护了该 Map。

---

## 14. 效果自定义数据与序列化

### 14.1 使用效果 Data Component

`BaseEffect` 本身实现了 `DataComponentHolder` 和 `MutableDataComponentHolder`。

适合频繁变化、需要参与复制和网络同步的数据，优先定义自己的 `DataComponentType<T>`，然后：

```java
set(MyDataComponents.MY_VALUE.value(), value);
Integer value = get(MyDataComponents.MY_VALUE.value());
remove(MyDataComponents.MY_VALUE.value());
```

这里传入的是 `DataComponentType<T>`，不是注册字段的 `DeferredHolder` 本身，因此需要调用 `.value()`（或 `.get()`）取得实际组件类型。

`BaseEffect.CODEC` 和 `BaseEffect.STREAM_CODEC` 会自动保存效果的 `DataComponentPatch`。

### 14.2 使用简单 NBT

也可以覆写：

```java
@Override
public CompoundTag saveSimpleData() {
    CompoundTag tag = super.saveSimpleData();
    tag.putFloat("bonus", bonus);
    return tag;
}

@Override
public void loadSimpleData(CompoundTag nbt) {
    super.loadSimpleData(nbt);
    bonus = nbt.getFloat("bonus");
}
```

这部分数据会被 `BaseEffect.CODEC` 和 `BaseEffect.STREAM_CODEC` 序列化。

### 14.3 `markChanged`

如果效果内部状态变化后需要让所属 `ItemStack` 被识别为变化，可以调用：

```java
markChanged(itemStack);
```

### 14.4 `copy()` 注意事项

`BaseEffect.copy()` 当前会：

- 根据原始类型、等级和装备类型创建新效果；
- 复制效果 Data Component；
- 若实现 `ISubEffectProvider`，调用 `copyFrom`。

它不会自动通过 `saveSimpleData/loadSimpleData` 复制普通 Java 字段。自定义字段如果需要在 `copy()` 中保留，建议：

- 把数据放进 Data Component；或
- 覆写 `copy()`；或
- 使用合适的 `copyFrom` 机制。

---

## 15. 动态修改 ItemStack 效果

框架定义两个物品效果组件：

```java
EEItemDataComponentTypes.ITEM_EFFECT
EEItemDataComponentTypes.ITEM_EFFECT_ADDITION
```

其中：

- `ITEM_EFFECT` 通常保存默认效果；
- `ITEM_EFFECT_ADDITION` 可用于运行时附加效果。

示例：

```java
ItemEffectsData current = stack.getOrDefault(
        EEItemDataComponentTypes.ITEM_EFFECT_ADDITION.value(),
        ItemEffectsData.EMPTY
);

BaseEffect newEffect = MyEquipmentEffectTypes.MY_EFFECT.value()
        .create(2, EquipmentTypes.CURIO);

stack.set(
        EEItemDataComponentTypes.ITEM_EFFECT_ADDITION.value(),
        current.withEffectAdded(newEffect)
);
```

`withEffectAdded` 返回新的 `ItemEffectsData`，不会修改原对象。

注意：当前 `withEffectAdded` 使用 `ImmutableMap.Builder.putAll(...).put(...)`。如果原数据中已经存在相同的 `EffectType`，构建时会因重复键抛出 `IllegalArgumentException`，不会自动覆盖旧效果。需要替换同类型效果时，应自行构造 Map：

```java
Map<EffectType<?>, BaseEffect> effects = new LinkedHashMap<>(current.effects());
effects.put(newEffect.getType(), newEffect);

stack.set(
        EEItemDataComponentTypes.ITEM_EFFECT_ADDITION.value(),
        ItemEffectsData.buildFromLinkedMap(effects)
);
```

如果修改的是已装备物品，需要确保相应槽位更新机制能够检测到 Data Component 的变化。

---

## 16. 子效果

效果可以实现：

```java
ISubEffectProvider<T>
```

接口：

```java
public interface ISubEffectProvider<T extends BaseEffect> {
    List<BaseEffect> getSubEffects(ItemStack itemStack);
    void copyFrom(T provider);
}
```

示例：

```java
public class ParentEffect extends BaseEffect
        implements ISubEffectProvider<ParentEffect> {

    private BaseEffect childEffect;

    public ParentEffect(
            EffectType<?> effectType,
            int level,
            EquipmentType equipmentType
    ) {
        super(effectType, level, equipmentType);
    }

    @Override
    public List<BaseEffect> getSubEffects(ItemStack itemStack) {
        return childEffect == null ? List.of() : List.of(childEffect);
    }

    @Override
    public void copyFrom(ParentEffect provider) {
        childEffect = provider.childEffect == null
                ? null
                : provider.childEffect.copy();
    }
}
```

`EquipmentEffectAPI.getEffects(...)` 会自动展开子效果，并按子效果的 `EffectType` 合并到结果 Map。

如果父效果和子效果具有相同 `EffectType`，后加入的子效果会覆盖同键效果。

---

## 17. 装备类型与装备来源

### 17.1 内置装备类型

| 字段 | 含义 |
|---|---|
| `EquipmentTypes.HEAD` | 头盔槽 |
| `EquipmentTypes.CHEST` | 胸甲槽 |
| `EquipmentTypes.LEGS` | 护腿槽 |
| `EquipmentTypes.FEET` | 鞋子槽 |
| `EquipmentTypes.HUMANOID_ARMOR` | 四个原版护甲槽 |
| `EquipmentTypes.MAIN_HAND` | 主手 |
| `EquipmentTypes.OFF_HAND` | 副手 |
| `EquipmentTypes.HANDS` | 主手和副手 |
| `EquipmentTypes.ARMOR_AND_HANDS` | 原版护甲与双手 |
| `EquipmentTypes.CURIO` | Curios 槽位 |
| `EquipmentTypes.ALL` | 查询过滤时匹配任意装备类型；其 `source()` 当前只枚举内置 Armor、Curios 和 Hands 来源 |
| `EquipmentTypes.NON` | 不匹配任何装备来源 |

### 17.2 自定义装备来源

实现 `IEquipmentSource`：

```java
public final class MyEquipmentSource implements IEquipmentSource {
    public static final MyEquipmentSource INSTANCE = new MyEquipmentSource();

    @Override
    public List<ItemStack> get(LivingEntity livingEntity) {
        return List.of(/* 从自定义容器读取物品 */);
    }
}
```

外部 Mod 建议创建自己的 `DeferredRegister`，不要在不确定加载顺序时直接向框架内置的 `EquipmentTypes.EQUIPMENT_TYPE` 添加条目：

```java
public final class MyEquipmentTypes {
    private static final DeferredRegister<EquipmentType> EQUIPMENT_TYPES =
            DeferredRegister.create(EERegisterTypes.EQUIPMENT_TYPE, MyMod.MODID);

    public static final EquipmentType MY_EQUIPMENT_TYPE = EquipmentTypes.register(
            EQUIPMENT_TYPES,
            "my_equipment_type",
            MyEquipmentSource.INSTANCE
    );

    public static final EquipmentType ARMOR_AND_CURIO = EquipmentTypes.register(
            EQUIPMENT_TYPES,
            "armor_and_curio",
            EquipmentTypes.HUMANOID_ARMOR,
            EquipmentTypes.CURIO
    );

    public static void register(IEventBus eventBus) {
        EQUIPMENT_TYPES.register(eventBus);
    }
}
```

在 Mod 初始化中注册：

```java
MyEquipmentTypes.register(modEventBus);
```

如果直接向 `EquipmentTypes.EQUIPMENT_TYPE` 注册条目，必须确保条目在 `EquipmentEffectLib.init(...)` 将该 `DeferredRegister` 注册到事件总线之前创建，否则可能错过注册阶段。

注意：仅注册 `EquipmentType` 不等于已完成自定义槽位监听。若自定义容器不走原版装备事件或 Curios 事件，还需实现自己的 `SlotEffectManager`、`ISlotContext` 和槽位变化通知逻辑。

---

## 18. 套装效果

### 18.1 注册套装效果

```java
public final class MySetEffects {
    private static final DeferredRegister<SetEffect> SET_EFFECTS =
            DeferredRegister.create(EERegisterTypes.SETS_EFFECT_TYPE_KEY, MyMod.MODID);

    public static final DeferredHolder<SetEffect, MyArmorSetEffect> MY_ARMOR_SET =
            SET_EFFECTS.register("my_armor_set", MyArmorSetEffect::new);

    public static void register(IEventBus eventBus) {
        SET_EFFECTS.register(eventBus);
    }
}
```

这里使用的是 `DeferredRegister.create(ResourceKey<Registry<T>>, modid)` 重载；装备效果注册示例则传入了已经创建的 `Registry<EffectType<?>>`。两种写法分别匹配当前源码中的注册表对象类型，不要在泛型不匹配时机械互换。

### 18.2 编写套装效果

```java
public class MyArmorSetEffect extends SetEffect {
    @Override
    public void appendToolTip(
            ItemStack itemStack,
            Item.TooltipContext tooltipContext,
            Player player,
            TooltipFlag tooltipFlag,
            List<Component> list
    ) {
        list.add(Component.literal("套装效果"));
    }

    @Override
    public EffectInstance createInstance(int piece) {
        return new MySetEffectInstance(piece);
    }
}
```

套装实例：

```java
public class MySetEffectInstance extends EffectInstance {
    public MySetEffectInstance(int piece) {
        super(piece);
    }

    @Override
    public void onPieceUpdate(LivingEntity livingEntity, int piece) {
        // 套装件数变化
    }

    @Override
    public void tick(LivingEntity living) {
        // 每 Tick 逻辑
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        // 套装完全卸下
    }
}
```

### 18.3 将套装绑定到物品

监听 `SetItemSetsEffectEvent`：

```java
@EventBusSubscriber(modid = MyMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class MySetEffectEvents {
    @SubscribeEvent
    public static void attachSetEffects(SetItemSetsEffectEvent event) {
        event.sets(
                MySetEffects.MY_ARMOR_SET.value(),
                EquipmentTypes.HUMANOID_ARMOR,
                MyItems.MY_HELMET.get(),
                MyItems.MY_CHESTPLATE.get(),
                MyItems.MY_LEGGINGS.get(),
                MyItems.MY_BOOTS.get()
        );
    }
}
```

单个物品可用：

```java
event.set(
        MySetEffects.MY_ARMOR_SET.value(),
        EquipmentTypes.HUMANOID_ARMOR,
        MyItems.MY_HELMET.get()
);
```

框架会把套装和装备类型作为 `SetEffectData` 写入物品默认组件。

### 18.4 查询套装件数

```java
int piece = MySetEffects.MY_ARMOR_SET.value()
        .getPiece(livingEntity);
```

服务端获取实例：

```java
MySetEffectInstance instance = EntitySetsEffectData.getSetInstance(
        livingEntity,
        MySetEffects.MY_ARMOR_SET.value(),
        MySetEffectInstance.class
);
```

该方法可返回 `null`。

### 18.5 `SetEffectData`

当前结构：

```java
public record SetEffectData(
        Holder<SetEffect> setEffect,
        Holder<EquipmentType> equipmentType
) {}
```

它通过 Holder Codec 和 Holder StreamCodec 完成持久化与网络同步。

---

## 19. 事件接口

### 19.1 `ITickAbleEffect`

```java
void effectTick(ItemStack itemStack, LivingEntity wearer)
```

用于每 Tick 装备效果。

### 19.2 `ICurioEffect`

继承 `ITickAbleEffect`，并提供：

```java
void modifyCurioAttribute(CurioAttributeModifierEvent event)
```

示例：

```java
public class MyCurioEffect extends BaseEffect implements ICurioEffect {
    // ...

    @Override
    public void modifyCurioAttribute(CurioAttributeModifierEvent event) {
        event.addModifier(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(MyMod.MODID, "curio_damage"),
                        2.0D,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
    }
}
```

当前框架只会遍历 `CurioAttributeModifierEvent#getItemStack()` 上的效果并调用该接口，不会自动遍历实体的全部 Curio 效果。需要跨槽位聚合时，应通过 `findBestEffect(...)` 或 `findStackableEffect(...)` 主动查询实体。

### 19.3 `IArmorEffect`

继承 `ITickAbleEffect`，当前是用于标记护甲效果的接口。

### 19.4 `IMainHandEffect`

```java
void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
```

`IMainHandEffect` 本身只定义接口。当前仓库中的攻击转发由 `com.chen1335.legendaryRelics.mixins.legendary_relics.ItemStackMixin` 实现，不属于 `equipmentEffectLib` 包内的通用转发逻辑。若把该框架拆成独立库使用，需要确认这个 Mixin 仍会加载，或自行实现攻击事件转发。

---

## 20. 数据组件和实体附件

### 20.1 物品数据组件

| 字段 | 类型 | 用途 |
|---|---|---|
| `ITEM_EFFECT` | `ItemEffectsData` | 物品默认效果 |
| `ITEM_EFFECT_ADDITION` | `ItemEffectsData` | 运行时附加效果 |
| `SET_EFFECT` | `SetEffectData` | 物品所属套装 |

它们都配置了持久化 `Codec` 和网络同步 `StreamCodec`。

### 20.2 效果内部数据组件

当前内置：

```java
EEItemEffectDataComponentTypes.EFFECT_LEVEL
EEItemEffectDataComponentTypes.WEARER_ARMOR
```

`EFFECT_LEVEL` 保存 `BaseEffect` 的原始等级。

### 20.3 实体附件

| 字段 | 类型 | 用途 |
|---|---|---|
| `ENTITY_EQUIPMENT_EFFECT_DATA` | `EntityEquipmentEffectData` | 当前槽位效果、等级排序、激活状态 |
| `ENTITY_SETS_EFFECT_DATA` | `EntitySetsEffectData` | 套装槽位、件数和实例 |

通常应通过 `EquipmentEffectAPI` 查询，而不是直接操作附件内部 Map。

---

## 21. 完整最小示例

### 21.1 效果类

```java
public class DamageBoostEffect extends BaseEffect {
    private static final ResourceLocation MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(MyMod.MODID, "damage_boost");

    public DamageBoostEffect(
            EffectType<?> effectType,
            int level,
            EquipmentType equipmentType
    ) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void onActive(ISlotContext context, LivingEntity entity, ItemStack stack) {
        AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage == null) return;

        ResourceLocation id = context.pathRL(MODIFIER_ID);
        damage.removeModifier(id);
        damage.addTransientModifier(new AttributeModifier(
                id,
                0.1D * getEffectLevel(entity, stack),
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));
    }

    @Override
    public void onDeActive(ISlotContext context, LivingEntity entity, ItemStack stack) {
        AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(context.pathRL(MODIFIER_ID));
        }
    }

    @Override
    public void appendToolTip(
            ItemStack stack,
            Item.TooltipContext context,
            Player player,
            TooltipFlag flag,
            List<Component> tooltip
    ) {
        tooltip.add(Component.literal(
                "攻击伤害提高 " + getRawEffectLevel() * 10 + "%"
        ));
    }
}
```

### 21.2 注册效果

```java
public final class MyEquipmentEffectTypes {
    private static final DeferredRegister<EffectType<?>> EFFECT_TYPES =
            DeferredRegister.create(EERegisterTypes.EQUIPMENT_EFFECT_TYPE, MyMod.MODID);

    public static final DeferredHolder<EffectType<?>, EffectType<DamageBoostEffect>> DAMAGE_BOOST =
            EFFECT_TYPES.register(
                    "damage_boost",
                    () -> new EffectType<>(DamageBoostEffect::new)
            );

    public static void register(IEventBus eventBus) {
        EFFECT_TYPES.register(eventBus);
    }
}
```

### 21.3 物品提供默认效果

```java
public class DamageRingItem extends Item implements IEffectEquipment {
    public DamageRingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public List<BaseEffect> getDefaultEffects() {
        return List.of(
                MyEquipmentEffectTypes.DAMAGE_BOOST.value()
                        .create(1, EquipmentTypes.CURIO)
        );
    }
}
```

### 21.4 初始化

```java
public MyMod(IEventBus eventBus, ModContainer container) {
    EquipmentEffectLib.init(eventBus, container);
    MyEquipmentEffectTypes.register(eventBus);
}
```

---

## 22. 当前实现注意事项

### 22.1 查询结果已经改为 `SlotEffectHolder`

当前签名是：

```java
Optional<SlotEffectHolder<T>> findBestEffect(...)
Optional<List<SlotEffectHolder<T>>> findStackableEffect(...)
```

旧代码中的：

```java
holder.effect()
holder.itemStack()
```

需要改为：

```java
holder.infoHolder().effect()
holder.infoHolder().itemStack()
```

### 22.2 `findBestEffect` 与内部最佳效果逻辑

`EntityEquipmentEffectData` 内部存在按最高等级选择效果的 `getBestEffect(...)`。

当前 `EquipmentEffectAPI.findBestEffect(...)` 则从 `getEffectsByType(...)` 的列表中返回第一个元素。使用时应以当前源码行为为准；若要求 API 与内部激活选择严格一致，建议让 API 直接调用实体数据的 `getBestEffect(...)`。

### 22.3 `stackable` 不会自动合并数值

注册时的 `stackable = true` 不会自动把等级或数值相加。聚合规则由具体效果在事件中实现，例如：

- 相加；
- 取最大值；
- 乘算；
- 按来源数量计算。

### 22.4 同一物品同类型效果只能有一个

`ItemEffectsData` 的键是 `EffectType<?>`。通过普通可变 Map 或 `AttachItemEffectEvent` 重复写入同一种类型时，后写入的实例会覆盖旧实例；但当前 `ItemEffectsData.withEffectAdded(...)` 使用 `ImmutableMap.Builder`，遇到重复类型会抛出重复键异常，具体替换方式见第 15 节。

### 22.5 Tick 会遍历全部来源

`EntityEquipmentEffectData.tick(...)` 遍历所有 `SlotEffectHolder`。实现 `ITickAbleEffect` 时，应注意同类型效果可能从多个槽位分别执行。

### 22.6 自定义装备来源还需要槽位监听

只注册 `EquipmentType` 不会自动让框架知道自定义容器何时变化。完整扩展通常还需要：

- 自定义 `SlotEffectManager`；
- 自定义 `ISlotContext`；
- 在槽位变化时调用管理器更新逻辑；
- 将管理器纳入实体效果数据管理。

### 22.7 自定义 Java 字段的复制

普通 Java 字段即使实现了 `saveSimpleData/loadSimpleData`，也不会被 `BaseEffect.copy()` 自动复制。需要使用 Data Component、覆写 `copy()` 或实现适当的 `copyFrom`。

### 22.8 事件总线

`AttachItemEffectEvent` 和 `SetItemSetsEffectEvent` 是由框架通过 `NeoForge.EVENT_BUS` 发布的运行时事件。监听器应注册到 GAME/NeoForge 事件总线，而不是 Mod 事件总线。

### 22.9 自定义注册表事件订阅归属

当前三个自定义注册表的 `NewRegistryEvent` 监听器位于 `equipmentEffectLib.common.EventHandler`，但其 `@EventBusSubscriber` 使用的是 `LegendaryRelics.MODID`。在当前整合项目中可以正常工作；若将 `equipmentEffectLib` 拆成独立 Mod 或单独发布的库，需要确认该订阅类会被加载，并将 modid 调整为独立库实际使用的 modid。

---

## 源码索引

主要源码位置：

```text
src/main/java/com/chen1335/equipmentEffectLib/
```

建议优先阅读：

```text
EquipmentEffectLib.java
API/EquipmentEffectAPI.java
API/IEffectEquipment.java
API/objects/EquipmentTypes.java
effectBase/EffectType.java
effectBase/BaseEffect.java
common/SlotEffectHolder.java
attachmentDatas/EntityEquipmentEffectData.java
slotEffectManagers/SlotEffectManager.java
equipmentSetEffect/SetEffect.java
attachmentDatas/EntitySetsEffectData.java
```
