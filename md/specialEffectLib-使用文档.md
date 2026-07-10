# specialEffectLib 使用文档

> 本文档基于项目当前源码编写，适用于当前分支中的 `com.chen1335.specialEffectLib` 实现。
>
> `specialEffectLib` 是一套独立于原版 `MobEffect` 的实体特殊效果系统，支持：
>
> - 按来源 UUID 隔离同类型效果；
> - 自定义添加、更新和合并规则；
> - 实体附件持久化；
> - 服务端到客户端的增量和全量同步；
> - 限时效果；
> - 属性修改器；
> - HUD 图标、持续时间遮罩和文字角标。

## 目录

- [1. 核心概念](#1-核心概念)
- [2. 初始化](#2-初始化)
- [3. 注册特殊效果类型](#3-注册特殊效果类型)
- [4. 编写普通特殊效果](#4-编写普通特殊效果)
- [5. 来源实体与来源 UUID](#5-来源实体与来源-uuid)
- [6. 给实体添加或更新效果](#6-给实体添加或更新效果)
- [7. 查询效果](#7-查询效果)
- [8. 效果生命周期](#8-效果生命周期)
- [9. 限时效果 TimeLimitEffect](#9-限时效果-timelimiteffect)
- [10. 可叠层效果 StackAbleEffect](#10-可叠层效果-stackableeffect)
- [11. 属性修改器](#11-属性修改器)
- [12. NBT 持久化](#12-nbt-持久化)
- [13. 网络序列化](#13-网络序列化)
- [14. 实体效果数据结构](#14-实体效果数据结构)
- [15. 网络同步](#15-网络同步)
- [16. HUD 图标渲染](#16-hud-图标渲染)
- [17. 完整普通限时效果示例](#17-完整普通限时效果示例)
- [18. 完整可叠层属性效果示例](#18-完整可叠层属性效果示例)
- [19. 主动移除效果](#19-主动移除效果)
- [20. 真实项目用例](#20-真实项目用例)
- [21. 当前实现注意事项](#21-当前实现注意事项)
- [22. 源码索引](#22-源码索引)

---

## 1. 核心概念

### 1.1 `MobEffectType<T>`

`MobEffectType<T extends SpecialMobEffect>` 是特殊效果的注册类型和实例工厂，负责：

- 创建效果实例；
- 标记是否需要 HUD 图标；
- 指定效果分类；
- 根据注册 ID 推导图标资源路径。

它不是原版 `MobEffect`，只是 `specialEffectLib` 自定义注册表中的类型。

### 1.2 `SpecialMobEffect`

`SpecialMobEffect` 是所有特殊效果的基础类，负责：

- 保存 `MobEffectType<?>`；
- 保存来源实体 UUID；
- Tick、过期、添加/更新和移除生命周期；
- NBT 持久化；
- 网络编码和解码；
- 属性修改器；
- HUD 角标文字和进度值。

### 1.3 `TimeLimitEffect`

`TimeLimitEffect` 继承 `SpecialMobEffect`，增加：

- `timeLeft`；
- `totalTime`；
- 每 Tick 倒计时；
- 时间归零后过期；
- HUD 剩余时间比例。

### 1.4 `EntityEffectData`

每个 `LivingEntity` 通过 Attachment 持有一个 `EntityEffectData`。

它的核心结构是：

```java
Map<UUID, Map<MobEffectType<?>, SpecialMobEffect>>
```

第一层按**来源实体 UUID**分组，第二层按**效果类型**分组。

因此：

- 同一来源、同一效果类型只能有一个实例；
- 不同来源可以同时给同一目标施加同类型效果；
- 合并器只处理同一来源、同一类型的旧效果。

### 1.5 `FinalEffectGetter<T>`

添加效果时可传入合并器：

```java
public interface FinalEffectGetter<T extends SpecialMobEffect> {
    T accept(T theNew, T theOld);
}
```

它决定同来源、同类型效果重复添加时最终保留哪个实例以及如何合并数据。

---

## 2. 初始化

在 Mod 构造方法中调用：

```java
public MyMod(IEventBus modEventBus, ModContainer modContainer) {
    SpecialEffectLib.init(modEventBus, modContainer);
    MySpecialEffects.register(modEventBus);
}
```

当前 `SpecialEffectLib.init(...)` 会注册：

```java
SEAttachmentTypes.ATTACHMENT_TYPES
```

其中包含：

```java
SEAttachmentTypes.ENTITY_EFFECT_DATA
```

自定义特殊效果注册表是：

```java
RegisterTypes.SPECIAL_EFFECT_TYPE
```

注册表键是：

```text
special_effect_lib:special_effect
```

该注册表启用了 `.sync(true)`，会同步到客户端。

### 当前整合项目的事件订阅归属

当前 `NewRegistryEvent`、网络包注册和游戏事件监听位于：

```text
com.chen1335.specialEffectLib.common.EventHandler
```

但它的 `@EventBusSubscriber` 使用的是 `LegendaryRelics.MODID`。在当前整合项目中可以工作；若把 `specialEffectLib` 拆成独立 Mod 或独立库，需要调整事件订阅 modid，并确认注册表、网络包和游戏事件监听器都会加载。

---

## 3. 注册特殊效果类型

### 3.1 创建 DeferredRegister

```java
public final class MySpecialEffects {
    private static final DeferredRegister<MobEffectType<?>> EFFECT_TYPES =
            DeferredRegister.create(
                    RegisterTypes.SPECIAL_EFFECT_TYPE,
                    MyMod.MODID
            );

    public static void register(IEventBus eventBus) {
        EFFECT_TYPES.register(eventBus);
    }
}
```

### 3.2 注册普通效果

```java
public static final DeferredHolder<MobEffectType<?>, MobEffectType<MyEffect>> MY_EFFECT =
        EFFECT_TYPES.register(
                "my_effect",
                MobEffectType.Builder.<MyEffect>builder()
                        .factory(MyEffect::new)
                        .build()
        );
```

工厂引用 `MyEffect::new` 要求效果类提供：

```java
public MyEffect(MobEffectType<?> effectType)
```

### 3.3 注册带 HUD 图标的效果

```java
public static final DeferredHolder<MobEffectType<?>, MobEffectType<MyEffect>> MY_EFFECT =
        EFFECT_TYPES.register(
                "my_effect",
                MobEffectType.Builder.<MyEffect>builder()
                        .factory(MyEffect::new)
                        .renderIcon()
                        .build()
        );
```

调用 `.renderIcon()` 会令：

```java
effectType.shouldRenderIcon() == true
```

不过当前 HUD 渲染器没有检查 `shouldRenderIcon()`，详见第 21 节。

### 3.4 设置效果分类

```java
public static final DeferredHolder<MobEffectType<?>, MobEffectType<MyEffect>> MY_EFFECT =
        EFFECT_TYPES.register(
                "my_effect",
                MobEffectType.Builder.<MyEffect>builder()
                        .factory(MyEffect::new)
                        .renderIcon()
                        .category(MobEffectCategory.HARMFUL)
                        .build()
        );
```

Builder 默认分类：

```java
MobEffectCategory.NEUTRAL
```

当前 HUD 分类方式：

- `HARMFUL`：减益列表；
- 其他分类：增益列表。

---

## 4. 编写普通特殊效果

最基础的效果类：

```java
public class MyEffect extends SpecialMobEffect {
    public MyEffect(MobEffectType<?> effectType) {
        super(effectType);
    }
}
```

### 4.1 运行时参数构造器

注册表反序列化需要工厂构造器；业务代码通常还需要一个接收参数的构造器：

```java
public class MyEffect extends SpecialMobEffect {
    private float strength;

    public MyEffect(MobEffectType<?> effectType) {
        super(effectType);
    }

    public MyEffect(float strength) {
        this(MySpecialEffects.MY_EFFECT.value());
        this.strength = strength;
    }
}
```

### 4.2 Tick

```java
@Override
public void tick(LivingEntity target) {
    super.tick(target);

    if (!target.level().isClientSide && target.tickCount % 20 == 0) {
        // 每秒服务端逻辑
    }
}
```

框架在 `EntityTickEvent.Pre` 中对所有 `LivingEntity` 的所有特殊效果调用 `tick(...)`。

当前事件没有只限制服务端，所以效果本身应根据逻辑明确判断：

```java
target.level().isClientSide
```

### 4.3 过期条件

普通 `SpecialMobEffect` 默认永不过期：

```java
@Override
public boolean isExpired() {
    return false;
}
```

自定义过期条件：

```java
@Override
public boolean isExpired() {
    return shouldRemove;
}
```

当效果过期时，`EntityEffectData.tick(...)` 会：

1. 从内部 Map 移除效果；
2. 调用 `effect.onRemove(target)`。

### 4.4 添加、更新和移除生命周期

```java
@Override
public void onAddOrUpdate(LivingEntity target) {
    // 首次添加、合并后更新或实体加入世界时调用
}

@Override
public void onRemove(LivingEntity target) {
    // 过期或实体离开世界时调用
}
```

注意：实体离开世界时会调用 `onRemove`，但这并不一定表示效果已从附件中删除；实体再次加入世界时，现有数据中的效果会再次调用 `onAddOrUpdate`。

---

## 5. 来源实体与来源 UUID

### 5.1 设置来源实体

```java
effect.setSourceEntity(attacker);
```

当前 `setSourceEntity(...)` 只保存：

```java
attacker.getUUID()
```

不会立即把实体对象写入内部缓存。

传入 `null` 时该方法不会清空旧来源，只会无操作。需要显式改为无来源时应调用：

```java
effect.setSourceEntityUUID(EntityEffectData.NO_SOURCE_UUID);
```

也可以直接设置 UUID：

```java
effect.setSourceEntityUUID(sourceUuid);
```

### 5.2 无来源效果

未设置来源时使用：

```java
EntityEffectData.NO_SOURCE_UUID
```

当前值：

```text
766e2f40-715f-4ca1-a9e2-a391a73b4ceb
```

所有未设置来源且类型相同的效果都处于同一个来源分组，会互相覆盖或合并。

### 5.3 获取来源实体

```java
@Nullable Entity source = effect.getSourceEntity(target.level());
```

当前行为：

- 无来源 UUID：返回 `null`；
- 服务端：通过 `ServerLevel#getEntity(UUID)` 解析并缓存；
- 客户端：没有已缓存实体时直接返回 `null`；
- 来源实体已移除：标记为 removed，之后返回 `null`。

因此客户端逻辑不应依赖 `getSourceEntity(...)` 得到来源实体。

### 5.4 来源隔离示例

攻击者 A 和攻击者 B 都给目标施加 `EROSION`：

```text
目标 EntityEffectData
├─ A 的 UUID
│  └─ EROSION -> Erosion 实例 A
└─ B 的 UUID
   └─ EROSION -> Erosion 实例 B
```

A 的后续攻击只会合并 A 的效果，不会合并 B 的效果。

这种隔离只发生在 `EntityEffectData` 的效果实例层面。若效果向原版 `AttributeInstance` 添加 modifier，还必须自行保证不同来源的 modifier ID 不冲突，否则后添加的 modifier 可能覆盖前一个来源的 modifier。

---

## 6. 给实体添加或更新效果

### 6.1 不需要合并旧效果

```java
MyEffect effect = new MyEffect(2.0F);
effect.setSourceEntity(attacker);

SpecialEffectAPI.addEffectToEntity(
        target,
        effect,
        null
);
```

当同来源、同类型旧效果存在时，传 `null` 会直接把 Map 中的旧实例替换为新实例。

注意：当前 API 在最终实例不同于旧实例时不会自动调用旧效果的 `onRemove(...)`。这不仅发生在合并器为 `null` 时，也发生在合并器返回新实例时。若旧效果添加过属性修改器或其他 transient 状态，可能产生残留。最安全的方式通常是更新并返回旧实例；确实需要替换时，应先清理旧实例，或修改 API 在 `old != null && old != effect` 时调用 `old.onRemove(target)`。

### 6.2 用新效果覆盖旧效果

```java
public MyEffect getFinal(MyEffect oldEffect) {
    return this;
}
```

该写法会用新实例替换旧实例，但当前 API 不会自动清理旧实例。它只适合旧效果没有属性修改器或其他需要 `onRemove(...)` 清理的状态。带 transient 状态的效果更推荐更新并返回 `oldEffect`。

添加：

```java
SpecialEffectAPI.addEffectToEntity(
        target,
        effect,
        MyEffect::getFinal
);
```

`FinalEffectGetter` 的参数顺序是：

```java
(theNew, theOld)
```

方法引用：

```java
MyEffect::getFinal
```

会在新效果实例上调用：

```java
theNew.getFinal(theOld)
```

### 6.3 保留旧实例并刷新数据

```java
public MyEffect getFinal(MyEffect oldEffect) {
    oldEffect.strength = this.strength;
    return oldEffect;
}
```

这种方式适合：

- 保留旧实例内部状态；
- 刷新持续时间；
- 增加层数；
- 避免旧实例属性修改器失去引用。

### 6.4 推荐只在服务端添加

```java
if (!target.level().isClientSide) {
    SpecialEffectAPI.addEffectToEntity(target, effect, MyEffect::getFinal);
}
```

`addEffectToEntity(...)` 在服务端会向客户端同步；客户端直接添加只影响本地附件，不会同步回服务端。

---

## 7. 查询效果

### 7.1 按来源 UUID 和类型查询

```java
Optional<MyEffect> effect = SpecialEffectAPI.getEffect(
        target,
        sourceUuid,
        MySpecialEffects.MY_EFFECT.value()
);
```

### 7.2 查询来源自身施加的 Buff

如果效果由实体给自己施加：

```java
SpecialEffectAPI.getEffect(
        livingEntity,
        livingEntity.getUUID(),
        MySpecialEffects.MY_EFFECT.value()
).ifPresent(effect -> {
    // 使用效果
});
```

`Doom` 和 `PhaseShooting` 当前就是这种模式。

### 7.3 获取实体完整效果数据

```java
EntityEffectData data =
        SpecialEffectAPI.getEntityEffectData(livingEntity);
```

读取全部效果：

```java
Map<UUID, Map<MobEffectType<?>, SpecialMobEffect>> effects =
        data.getEffects();
```

读取某个来源的效果 Map：

```java
Map<MobEffectType<?>, SpecialMobEffect> sourceEffects =
        data.getSourceEffects(sourceUuid);
```

注意：`getSourceEffects(...)` 使用 `computeIfAbsent`，纯查询不存在的来源时也会创建一个空 Map。

### 7.4 判断任意来源是否存在某种效果

```java
boolean hasEffect = data.hasEffect(
        livingEntity,
        MySpecialEffects.MY_EFFECT.value()
);
```

当前 `hasEffect` 的 `livingEntity` 参数没有被使用，它只是遍历所有来源 Map 检查类型。

---

## 8. 效果生命周期

### 8.1 添加或更新

服务端调用：

```java
SpecialEffectAPI.addEffectToEntity(...)
```

后会立即调用：

```java
effect.onAddOrUpdate(target)
```

### 8.2 每 Tick

框架对客户端和服务端 LivingEntity 都会执行：

```java
effect.tick(target)
```

随后检查：

```java
effect.isExpired()
```

如果过期：

```java
effect.onRemove(target)
```

并从 Map 删除。

### 8.3 实体加入世界

实体加入世界时，框架会遍历附件中已有的所有效果并调用：

```java
effect.onAddOrUpdate(target)
```

适合恢复由效果添加的 transient 属性修改器。

### 8.4 实体离开世界

实体离开世界时，框架会遍历所有效果并调用：

```java
effect.onRemove(target)
```

适合临时清理属性修改器。

---

## 9. 限时效果 `TimeLimitEffect`

### 9.1 基础实现

```java
public class MyTimedEffect extends TimeLimitEffect {
    public MyTimedEffect(MobEffectType<?> effectType) {
        super(effectType);
    }

    public MyTimedEffect(int duration) {
        this(MySpecialEffects.MY_TIMED_EFFECT.value());
        initTime(duration);
    }
}
```

设置总时间和剩余时间：

```java
effect.initTime(20 * 10); // 10 秒
```

`initTime(time)` 同时设置：

```java
timeLeft = time;
totalTime = time;
```

也可以分别修改：

```java
effect.setTimeLeft(100);
effect.setTotalTime(200);
```

### 9.2 过期

`TimeLimitEffect` 每 Tick：

```java
timeLeft--;
```

过期条件：

```java
timeLeft <= 0
```

### 9.3 HUD 百分比

```java
float percentage = effect.getPercentage();
```

当前计算：

```java
(float) timeLeft / (float) totalTime
```

HUD 会使用该值绘制扇形覆盖层。

必须确保 `totalTime > 0`。默认构造后若没有调用 `initTime(...)` 或设置总时间，当前实现可能产生 `NaN` 或无穷值。

### 9.4 时间字段已自动序列化

`TimeLimitEffect` 已实现：

- NBT `save/load`；
- 网络 `encode/decode`。

子类无需重复保存 `timeLeft` 和 `totalTime`，但仍需保存自己的字段。

---

## 10. 可叠层效果 `StackAbleEffect`

> `StackAbleEffect` 当前不属于 `com.chen1335.specialEffectLib`，而是 LegendaryRelics 在以下包中的项目级扩展：
>
> ```text
> com.chen1335.legendaryRelics.registers.specialMobEffects.common.StackAbleEffect
> ```
>
> 如果把 `specialEffectLib` 独立使用，需要复制、迁移或自行实现该类。

### 10.1 核心字段

```java
protected int decayTime = 0;
protected int stack = 1;
```

### 10.2 合并层数

默认合并方法：

```java
public <T extends StackAbleEffect> T getFinal(T theOld) {
    theOld.initTime(totalTime);
    theOld.stack = theOld.stack + stack;
    theOld.stack = Math.min(theOld.stack, getMaxStack());
    return theOld;
}
```

使用示例：

```java
MyStackEffect effect = new MyStackEffect(valuePerStack);
effect.initTime(20 * 5);
effect.setDecayTime(20);
effect.setSourceEntity(attacker);

SpecialEffectAPI.addEffectToEntity(
        target,
        effect,
        MyStackEffect::getFinal
);
```

调用合并前必须先 `initTime(...)`。否则 `getFinal` 可能把旧效果刷新为 0 时长。

### 10.3 最大层数

```java
@Override
public int getMaxStack() {
    return 10;
}
```

### 10.4 逐层衰减

当 `timeLeft <= 0` 时：

- `stack > 1 && decayTime > 0`：
  - 把时间重置为 `decayTime`；
  - 层数减一；
  - 调用 `onStackChange(target)`；
- 否则保持/重置为 1，并在过期检查中移除。

覆写层数变化：

```java
@Override
protected void onStackChange(LivingEntity target) {
    addAttributeModifiers(target.getAttributes());
}
```

如果叠层效果的属性值依赖层数，必须在自然掉层时刷新属性修改器。

### 10.5 整体过期而非逐层衰减

不调用：

```java
setDecayTime(...)
```

时，`decayTime` 保持 0。时间归零后效果会整体过期，不会逐层衰减。

### 10.6 HUD 层数

`StackAbleEffect.getString()` 返回：

```java
String.valueOf(stack)
```

HUD 会把这个字符串绘制在图标上。

---

## 11. 属性修改器

`SpecialMobEffect` 内置属性修改器管理。

### 11.1 使用固定 ResourceLocation 注册

```java
private static final ResourceLocation ATTACK_DAMAGE =
        ResourceLocation.fromNamespaceAndPath(MyMod.MODID, "my_effect.damage");

public MyAttributeEffect(MobEffectType<?> effectType) {
    super(effectType);

    registerModifier(
            Attributes.ATTACK_DAMAGE,
            ATTACK_DAMAGE,
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
            amplifier -> amplifier * damagePerLevel
    );
}
```

固定 `ResourceLocation` 只适合以下情况：

- 同一目标逻辑上只允许一个该效果实例；
- 效果总是由目标自身施加给自身；
- 不同来源不需要同时叠加属性，而是允许后一次覆盖前一次。

来源 UUID 只隔离效果 Map，不会隔离 `AttributeInstance` 的 modifier ID。若同类型属性效果需要按不同来源同时生效，应使用按来源稳定生成且可持久化的唯一 modifier ID，或者设计为单个聚合效果统一计算所有来源贡献。

### 11.2 使用字符串注册随机 ID

```java
registerModifier(
        Attributes.ARMOR,
        "my_effect_armor",
        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
        amplifier -> amplifier * armorPerLevel
);
```

字符串重载会在构造时通过 `LRUtil.randomLocation(10)` 创建随机 `ResourceLocation`，然后尝试通过 NBT 保存该 ID。

由于当前 modifier ID 持久化存在缺陷，优先建议使用固定 `ResourceLocation`，并参见第 21 节。

### 11.3 添加属性修改器

```java
@Override
public void onAddOrUpdate(LivingEntity target) {
    addAttributeModifiers(target.getAttributes());
}
```

内部会：

1. 移除同 ID 的旧 modifier；
2. 创建新的 transient modifier；
3. 使用 `getModifierAmplifier()` 计算数值。

### 11.4 移除属性修改器

```java
@Override
public void onRemove(LivingEntity target) {
    removeAttributeModifiers(target.getAttributes());
}
```

### 11.5 自定义 amplifier

```java
@Override
public int getModifierAmplifier() {
    return stack;
}
```

普通效果默认返回 1。

### 11.6 叠层属性效果

如果属性值依赖 `stack`：

```java
@Override
protected void onStackChange(LivingEntity target) {
    addAttributeModifiers(target.getAttributes());
}
```

`Doom` 和 `Erosion` 当前采用这种方式。

---

## 12. NBT 持久化

### 12.1 基类自动保存的数据

`SpecialMobEffect.save()` 自动保存：

```text
SourceEntityUUID
EffectType
modifiersId
```

`EntityEffectData` 会把所有效果的 `save()` 结果写入：

```text
Data: [ ... ]
```

反序列化时根据 `EffectType` 从注册表创建实例，再调用：

```java
effect.load(tag)
```

### 12.2 保存子类字段

```java
@Override
public CompoundTag save() {
    CompoundTag tag = super.save();
    tag.putFloat("Strength", strength);
    return tag;
}

@Override
public void load(CompoundTag tag) {
    super.load(tag);
    strength = tag.getFloat("Strength");
}
```

必须调用 `super.save()` 和 `super.load(...)`，否则会丢失来源 UUID、类型、时间或叠层信息。

### 12.3 `TimeLimitEffect` 子类

`TimeLimitEffect` 已保存：

```text
TotalTime
TimeLeft
```

子类只需追加自己的字段。

### 12.4 `StackAbleEffect` 子类

项目中的 `StackAbleEffect` 已保存：

```text
decayTime
stack
```

子类只需追加自己的数值字段。

---

## 13. 网络序列化

### 13.1 基类 StreamCodec 流程

`SpecialMobEffect.STREAM_CODEC` 会：

1. 编码 `MobEffectType` 注册表值；
2. 调用实例的 `encode(buffer)`。

解码时：

1. 解码 `MobEffectType`；
2. 调用 `effectType.create()` 创建实例；
3. 调用实例的 `decode(buffer)`。

### 13.2 基类自动同步的数据

`SpecialMobEffect` 默认只同步：

```java
sourceEntityUUID
```

### 13.3 子类网络字段

```java
@Override
public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
    super.encode(buffer);
    buffer.writeFloat(strength);
}

@Override
public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
    super.decode(buffer);
    strength = buffer.readFloat();
}
```

编码与解码顺序必须完全一致。

正确：

```java
// encode
buffer.writeFloat(strength);
buffer.writeInt(level);

// decode
strength = buffer.readFloat();
level = buffer.readInt();
```

错误的顺序会导致网络数据错位。

### 13.4 持久化和网络序列化都要实现

如果字段既需要跨世界保存，又需要客户端显示或使用，应同时实现：

- `save/load`；
- `encode/decode`。

只实现其中一组不能覆盖另一种场景。

---

## 14. 实体效果数据结构

### 14.1 获取数据

```java
EntityEffectData data =
        SpecialEffectAPI.getEntityEffectData(livingEntity);
```

等价于：

```java
livingEntity.getData(SEAttachmentTypes.ENTITY_EFFECT_DATA)
```

### 14.2 数据结构

```java
Map<UUID, Map<MobEffectType<?>, SpecialMobEffect>> effects
```

示例：

```text
sourceUuidA
├─ erosion -> Erosion
└─ inferno_scorch -> InfernoScorch

sourceUuidB
└─ erosion -> Erosion
```

### 14.3 放入效果

```java
data.putEffect(effect.getEffectType(), effect);
```

该方法根据：

```java
effect.getSourceEntityUUID()
```

选择第一层 Map。

通常应优先使用：

```java
SpecialEffectAPI.addEffectToEntity(...)
```

因为它还会处理生命周期和网络同步。

### 14.4 Attachment 持久化

`ENTITY_EFFECT_DATA` 使用：

```java
AttachmentType.serializable(...)
```

因此实体效果数据会通过 `EntityEffectData.serializeNBT/deserializeNBT` 持久化。

---

## 15. 网络同步

### 15.1 增量同步

服务端每次调用：

```java
SpecialEffectAPI.addEffectToEntity(...)
```

后，会发送：

```java
AddOrUpdateEffectPack
```

包内容：

- 目标实体 ID；
- 一个 `SpecialMobEffect`。

发送范围：

- 目标是 `ServerPlayer`：发送给该玩家自己；
- 目标是其他 LivingEntity：发送给正在追踪该实体的玩家。

当目标是玩家时，当前实现不会把增量更新发送给正在追踪该玩家的其他客户端。因此该策略适合“玩家本人 HUD”；若其他客户端也需要实时知道该玩家身上的效果，应同时发送给本人和追踪者。

客户端收到后只会：

```java
data.putEffect(type, effect)
```

不会调用客户端的 `onAddOrUpdate(...)`。

### 15.2 全量同步

以下情况发送：

- 玩家开始追踪某个 LivingEntity；
- 玩家登录并同步自身数据。

使用包：

```java
SyncAllEffectPack
```

客户端收到后直接替换实体的：

```java
SEAttachmentTypes.ENTITY_EFFECT_DATA
```

该处理只执行 `setData(...)`：不会对被替换的旧效果调用 `onRemove(...)`，也不会对新效果调用 `onAddOrUpdate(...)`。因此客户端初始化逻辑不应依赖全量同步触发生命周期；确有需要时，应修改包处理器显式处理，或让客户端逻辑能够惰性初始化。

### 15.3 效果移除同步限制

当前没有专门的“移除效果”网络包。

客户端和服务端都会 Tick `TimeLimitEffect`，因此限时效果通常会在两端分别过期。但：

- 主动服务端移除没有增量移除包；
- 客户端倒计时与服务端状态可能存在偏差；
- 需要立即、权威地同步主动移除时，应新增移除包或发送全量数据。

---

## 16. HUD 图标渲染

### 16.1 图标路径规则

`MobEffectType.getIcon()` 根据注册 ID 自动生成：

```text
<namespace>:textures/icon/special_effects/<path>.png
```

例如注册 ID：

```text
my_mod:my_effect
```

资源文件应放在：

```text
src/main/resources/assets/my_mod/textures/icon/special_effects/my_effect.png
```

建议使用 16×16 PNG。

### 16.2 图标分类

当前 `SpecialEffectIconRender`：

- `MobEffectCategory.HARMFUL` 渲染到减益侧；
- `BENEFICIAL` 和 `NEUTRAL` 渲染到增益侧。

布局基于玩家 HUD 的：

```java
gui.leftHeight
gui.rightHeight
```

### 16.3 持续时间遮罩

`IconRenderer` 会检查：

```java
effect.getPercentage() >= 0
```

满足时调用：

```java
RenderUtils.drawSector(...)
```

`TimeLimitEffect` 已覆写 `getPercentage()`，所以会自动显示剩余时间扇形。

普通效果默认返回：

```java
-1
```

因此不绘制扇形。

### 16.4 图标角标文字

`IconRenderer` 会绘制：

```java
effect.getString()
```

普通效果默认返回空字符串。

可自定义：

```java
@Override
public String getString() {
    return String.valueOf(level);
}
```

项目中的 `StackAbleEffect` 返回当前层数。

### 16.5 GUI Layer 注册

客户端层注册 ID：

```text
special_effect_lib:special_effect_icon
```

当前注册通过：

```java
event.registerAboveAll(...)
```

当前客户端事件订阅类使用 `LegendaryRelics.MODID`，若将库独立化需调整。

---

## 17. 完整普通限时效果示例

### 17.1 效果类

```java
public class BurningMarkEffect extends TimeLimitEffect {
    private float damagePerSecond;

    public BurningMarkEffect(MobEffectType<?> effectType) {
        super(effectType);
    }

    public BurningMarkEffect(float damagePerSecond, int duration) {
        this(MySpecialEffects.BURNING_MARK.value());
        this.damagePerSecond = damagePerSecond;
        initTime(duration);
    }

    @Override
    public void tick(LivingEntity target) {
        super.tick(target);

        if (!target.level().isClientSide
                && target.level().getGameTime() % 20 == 0) {
            Entity source = getSourceEntity(target.level());
            target.hurt(target.damageSources().magic(), damagePerSecond);
        }
    }

    public BurningMarkEffect getFinal(BurningMarkEffect oldEffect) {
        // 新效果覆盖旧效果并重置持续时间
        return this;
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = super.save();
        tag.putFloat("DamagePerSecond", damagePerSecond);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        damagePerSecond = tag.getFloat("DamagePerSecond");
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(damagePerSecond);
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        damagePerSecond = buffer.readFloat();
    }
}
```

### 17.2 注册

```java
public static final DeferredHolder<MobEffectType<?>, MobEffectType<BurningMarkEffect>> BURNING_MARK =
        EFFECT_TYPES.register(
                "burning_mark",
                MobEffectType.Builder.<BurningMarkEffect>builder()
                        .factory(BurningMarkEffect::new)
                        .renderIcon()
                        .category(MobEffectCategory.HARMFUL)
                        .build()
        );
```

图标路径：

```text
assets/my_mod/textures/icon/special_effects/burning_mark.png
```

### 17.3 应用

```java
if (!target.level().isClientSide) {
    BurningMarkEffect effect = new BurningMarkEffect(2.0F, 20 * 6);
    effect.setSourceEntity(attacker);

    SpecialEffectAPI.addEffectToEntity(
            target,
            effect,
            BurningMarkEffect::getFinal
    );
}
```

### 17.4 查询

```java
SpecialEffectAPI.getEffect(
        target,
        attacker.getUUID(),
        MySpecialEffects.BURNING_MARK.value()
).ifPresent(effect -> {
    int timeLeft = effect.getTimeLeft();
});
```

---

## 18. 完整可叠层属性效果示例

此示例依赖项目级 `StackAbleEffect`。

```java
public class PowerStackEffect extends StackAbleEffect {
    private static final ResourceLocation DAMAGE_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(MyMod.MODID, "power_stack.damage");

    private float damagePerStack;

    public PowerStackEffect(MobEffectType<?> effectType) {
        super(effectType);
        registerModifier(
                Attributes.ATTACK_DAMAGE,
                DAMAGE_MODIFIER,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                stack -> stack * damagePerStack
        );
    }

    public PowerStackEffect(float damagePerStack) {
        this(MySpecialEffects.POWER_STACK.value());
        this.damagePerStack = damagePerStack;
    }

    @Override
    protected void onStackChange(LivingEntity target) {
        addAttributeModifiers(target.getAttributes());
    }

    @Override
    public void onAddOrUpdate(LivingEntity target) {
        addAttributeModifiers(target.getAttributes());
    }

    @Override
    public void onRemove(LivingEntity target) {
        removeAttributeModifiers(target.getAttributes());
    }

    public PowerStackEffect getFinal(PowerStackEffect oldEffect) {
        PowerStackEffect result = super.getFinal(oldEffect);
        result.damagePerStack = damagePerStack;
        return result;
    }

    @Override
    public int getMaxStack() {
        return 10;
    }

    @Override
    public int getModifierAmplifier() {
        return stack;
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = super.save();
        tag.putFloat("DamagePerStack", damagePerStack);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        damagePerStack = tag.getFloat("DamagePerStack");
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(damagePerStack);
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        damagePerStack = buffer.readFloat();
    }
}
```

应用：

```java
PowerStackEffect effect = new PowerStackEffect(0.05F);
effect.initTime(20 * 5);
effect.setDecayTime(20);
effect.setSourceEntity(player);

SpecialEffectAPI.addEffectToEntity(
        player,
        effect,
        PowerStackEffect::getFinal
);
```

每次重复应用：

- 增加一层；
- 最多 10 层；
- 刷新 5 秒保持时间；
- 保持时间结束后每秒减少一层；
- HUD 显示层数和剩余时间扇形。

---

## 19. 主动移除效果

当前 `SpecialEffectAPI` 没有公开的 `removeEffect(...)` 方法。

### 19.1 当前可行的服务端手动移除方式

```java
EntityEffectData data = SpecialEffectAPI.getEntityEffectData(target);
Map<MobEffectType<?>, SpecialMobEffect> sourceEffects =
        data.getEffects().get(sourceUuid);

if (sourceEffects != null) {
    SpecialMobEffect removed = sourceEffects.remove(
            MySpecialEffects.MY_EFFECT.value()
    );

    if (removed != null) {
        removed.onRemove(target);
    }

    if (sourceEffects.isEmpty()) {
        data.getEffects().remove(sourceUuid);
    }
}
```

但这种方式不会自动向客户端发送移除同步。

### 19.2 推荐扩展 API

如果项目频繁主动移除效果，建议给 `SpecialEffectAPI` 添加：

```java
removeEffectFromEntity(target, sourceUuid, effectType)
```

并同时实现：

- 调用 `onRemove`；
- 清理空来源 Map；
- 向追踪客户端发送移除包或全量同步。

---

## 20. 真实项目用例

### 20.1 `Erosion`

特点：

- 继承 `StackAbleEffect`；
- 最多 5 层；
- 每秒按层数造成伤害；
- 每层降低护甲；
- 来源是攻击者；
- 没有设置 `decayTime`，到期后整体移除。

添加方式：

```java
Erosion erosion = new Erosion(perLayerDamage, armorReducePerLayer);
erosion.setSourceEntity(attacker);
erosion.initTime(100);
SpecialEffectAPI.addEffectToEntity(target, erosion, Erosion::getFinal);
```

`Erosion` 当前实现了 `save/load`，服务端持久化能够恢复 `perLayerDamage` 和 `armorReducePerLayer`；但它没有实现 `encode/decode`，这两个自定义数值不会同步到客户端。当前 HUD 只使用时间和层数，通常不受影响；若客户端需要显示或使用这些数值，应补齐网络序列化。

### 20.2 `Doom`

特点：

- 施加给攻击者自身；
- 最多 10 层；
- 每层增加交互距离和攻击伤害；
- 设置 `decayTime` 后逐层衰减；
- 层数变化时刷新属性修改器。

添加方式：

```java
Doom effect = new Doom(attackRangePerStack, damagePerStack);
effect.setDecayTime(20);
effect.initTime(keepTimeTicks);
effect.setSourceEntity(attacker);
SpecialEffectAPI.addEffectToEntity(attacker, effect, Doom::getFinal);
```

查询自身 Buff：

```java
SpecialEffectAPI.getEffect(
        attacker,
        attacker.getUUID(),
        LRSpecialMobEffects.DOOM.value()
);
```

### 20.3 `PhaseShooting`

特点：

- 施加给射手自身；
- 最多 10 层；
- 每层提高拉弓速度和箭矢伤害；
- 10 层触发额外射击逻辑。

添加方式：

```java
PhaseShooting effect = new PhaseShooting(
        drawSpeedPerStack,
        arrowDamagePerStack
);
effect.setDecayTime(20);
effect.initTime(keepTimeTicks);
effect.setSourceEntity(attacker);
SpecialEffectAPI.addEffectToEntity(
        attacker,
        effect,
        PhaseShooting::getFinal
);
```

### 20.4 `InfernoScorch`

特点：

- 继承 `TimeLimitEffect`；
- 每秒造成伤害；
- 客户端生成粒子；
- 新效果覆盖旧效果；
- 死亡时可扩散到附近目标。

它当前没有保存和同步自定义的 `damage`、`explosionDamage` 字段，使用时应补齐，详见第 21 节。

---

## 21. 当前实现注意事项

### 21.1 来源 UUID 判断使用了 `==`

`SpecialMobEffect.getSourceEntity(...)` 当前使用：

```java
sourceEntityUUID == EntityEffectData.NO_SOURCE_UUID
```

UUID 应按值比较。经过 NBT 或网络解码后，即使值等于 `NO_SOURCE_UUID`，引用也可能不同。建议修正为：

```java
EntityEffectData.NO_SOURCE_UUID.equals(sourceEntityUUID)
```

### 21.2 `setSourceEntity` 只保存 UUID

当前：

```java
setSourceEntity(entity)
```

只写入 `entity.getUUID()`，不会立即缓存实体对象。服务端第一次调用 `getSourceEntity(...)` 时再按 UUID 查找，客户端则通常返回 `null`。

### 21.3 modifier ID 的 NBT 读取层级错误

保存时 modifier ID 被写入：

```text
modifiersId: { ... }
```

但当前 `load(...)` 取出子 Compound 后没有使用，随后仍从外层 Compound 读取 ID。随机 modifier ID 可能无法正确恢复。

建议修改为：

```java
CompoundTag modifiersId = compoundTag.getCompound("modifiersId");
for (ModifierEntry modifier : attributeModifiers) {
    modifier.readRl(modifiersId);
}
```

### 21.4 固定 ResourceLocation modifier 的 NBT key 可能冲突

`ModifierEntry(ResourceLocation rl, ...)` 当前使用：

```java
id = rl.getNamespace();
```

同 namespace 的多个 modifier 会使用相同 NBT key。例如两个 `legendary_relics:*` modifier 会互相覆盖保存值。

建议至少使用完整路径或完整 ID，例如：

```java
id = rl.toString();
```

并确保 key 满足 CompoundTag 字符串键需求。

### 21.5 `EntityEffectData.tick` 遇到空来源 Map 会停止整个循环

当前空来源 Map 被移除后使用：

```java
break;
```

这会让本 Tick 后续来源的效果全部跳过。更合理的是：

```java
continue;
```

### 21.6 `deserializeNBT` 不清空旧数据

当前反序列化直接向已有 Map 写入，没有：

```java
effects.clear();
```

若同一个 `EntityEffectData` 实例被重复反序列化，旧数据可能残留。

### 21.7 覆盖旧效果不会自动调用 `onRemove`

只要最终写入 Map 的实例与旧实例不同，当前 API 都不会自动调用旧实例的 `onRemove(...)`。这既包括 `finalEffectGetter == null` 的直接替换，也包括合并器返回新实例（例如 `return this`）的情况。旧属性修改器或其他 transient 状态可能残留。

推荐合并器更新并返回旧实例。若确实要替换，建议修改 API，在 `old != null && old != effect` 时先调用：

```java
old.onRemove(target);
```

### 21.8 当前没有主动移除同步

限时效果依靠客户端和服务端分别 Tick 过期。主动服务端移除不会自动通知客户端，需要新增移除包或发送全量同步。

### 21.9 HUD 没有检查 `shouldRenderIcon()`

虽然 `MobEffectType` 保存了：

```java
renderIcon
```

但当前 `SpecialEffectIconRender` 会把玩家身上的所有效果都加入渲染列表，没有检查：

```java
effect.getEffectType().shouldRenderIcon()
```

因此没有调用 `.renderIcon()` 的效果仍可能被尝试渲染，并出现缺失纹理。

建议分类前增加：

```java
if (!effect.getEffectType().shouldRenderIcon()) {
    continue;
}
```

### 21.10 图标资源必须存在

`getIcon()` 只根据注册 ID生成路径，不检查文件是否存在。标记 `.renderIcon()` 后必须提供对应 PNG。

当前项目中 `EROSION` 已标记为显示图标，但检索时未发现对应的 `erosion.png`，应补充：

```text
assets/legendary_relics/textures/icon/special_effects/erosion.png
```

### 21.11 `TimeLimitEffect.getPercentage()` 可能除以 0

如果 `totalTime == 0`：

```java
timeLeft / totalTime
```

会产生无效值。建议：

```java
@Override
public float getPercentage() {
    if (totalTime <= 0) {
        return -1;
    }
    return Mth.clamp((float) timeLeft / totalTime, 0.0F, 1.0F);
}
```

### 21.12 `PhaseShooting` 自然掉层时没有刷新属性

`PhaseShooting` 当前没有覆写 `onStackChange(...)`。自然衰减使 `stack--` 后，属性 modifier 可能仍保留旧层数的数值，直到下一次添加/更新或移除。

建议与 `Doom` 一样覆写：

```java
@Override
protected void onStackChange(LivingEntity target) {
    addAttributeModifiers(target.getAttributes());
}
```

### 21.13 `InfernoScorch` 未保存和同步伤害字段

当前 `damage` 和 `explosionDamage` 只在业务构造器中赋值，没有实现：

- `save/load`；
- `encode/decode`。

世界重载或网络同步后可能恢复为默认值。应补齐两组序列化方法。

### 21.14 内外 Map 重复编码效果类型

`EntityEffectData.STREAM_CODEC` 的内层 Map 已编码 `MobEffectType` 作为 key，而 `SpecialMobEffect.STREAM_CODEC` 又在 value 中编码一次效果类型。功能上可以工作，但存在冗余。

### 21.15 客户端增量包不调用 `onAddOrUpdate`

`AddOrUpdateEffectPack` 客户端处理器只调用：

```java
data.putEffect(...)
```

若某个效果需要客户端 `onAddOrUpdate(...)` 执行额外初始化，当前增量同步不会触发它。HUD 读取字段通常不受影响。

### 21.16 Tick 同时发生在客户端和服务端

框架没有在统一 Tick 入口检查 side。效果实现应自行区分：

- 服务端伤害、生成实体、修改权威状态；
- 客户端粒子、视觉效果。

### 21.17 `StackAbleEffect` 不是 specialEffectLib 核心类

文档中的叠层示例依赖 LegendaryRelics 自己的扩展类。若独立发布 `specialEffectLib`，应考虑把通用叠层基类迁入库包，避免外部使用者依赖主 Mod 的业务包。

### 21.18 specialEffectLib 仍与 LegendaryRelics 存在硬耦合

当前不能只复制 `com.chen1335.specialEffectLib` 包就作为完全独立的库使用。独立化时至少需要处理：

1. `SpecialMobEffect` 对 `LRUtil.randomLocation(...)` 的依赖；
2. `IconRenderer` 对 `LegendaryRelics.client.RenderUtils` 的依赖；
3. `EventHandler` 和 `SpecialEffectClientEventHandler` 使用的 `LegendaryRelics.MODID`；
4. 注册 `NewRegistryEvent`、网络 Payload、实体 Tick/加入/离开、玩家追踪/登录和 GUI Layer 事件；
5. 调用 `SEAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus)` 并注册业务 Mod 自己的效果类型。

---

## 22. 源码索引

核心源码：

```text
src/main/java/com/chen1335/specialEffectLib/
```

建议优先阅读：

```text
SpecialEffectLib.java
API/SpecialEffectAPI.java
API/objects/RegisterTypes.java
API/objects/SEAttachmentTypes.java
mobEffect/MobEffectType.java
mobEffect/SpecialMobEffect.java
mobEffect/TimeLimitEffect.java
attachmentDatas/EntityEffectData.java
common/EventHandler.java
network/AddOrUpdateEffectPack.java
network/SyncAllEffectPack.java
client/IconRenderer.java
client/SpecialEffectIconRender.java
```

LegendaryRelics 真实扩展：

```text
src/main/java/com/chen1335/legendaryRelics/API/objects/LRSpecialMobEffects.java
src/main/java/com/chen1335/legendaryRelics/registers/specialMobEffects/common/StackAbleEffect.java
src/main/java/com/chen1335/legendaryRelics/registers/specialMobEffects/Erosion.java
src/main/java/com/chen1335/legendaryRelics/registers/specialMobEffects/Doom.java
src/main/java/com/chen1335/legendaryRelics/registers/specialMobEffects/PhaseShooting.java
src/main/java/com/chen1335/legendaryRelics/registers/specialMobEffects/InfernoScorch.java
```
