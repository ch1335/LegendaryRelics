package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects;

import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffects;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * 减速：按比例降低携带者的移动速度。
 *
 * <p>减速比例可由施加方自定义。多个来源同时存在时**只有幅度最大的那个生效**
 * （不叠加），所以所有来源共用同一个修饰符 id —— 移动速度上任何时刻只会挂一个减速修饰符。
 */
public class Slowness extends TimeLimitEffect {

    /** 默认减速比例，0.3 表示移动速度降低 30%。 */
    public static final float DEFAULT_SLOWNESS = 0.3F;

    /**
     * 所有减速来源共用的修饰符 id。
     *
     * <p>必须固定：{@link AttributeInstance#removeModifier(ResourceLocation)} 依赖它把上一个
     * 来源的修饰符摘掉，从而保证同时只有一个减速生效。
     */
    public static final ResourceLocation MODIFIER_ID = LegendaryRelics.id("slowness");

    private float slowness = DEFAULT_SLOWNESS;

    public Slowness(MobEffectType<?> effectType) {
        super(effectType);
    }

    public Slowness(float slowness) {
        this(LRSpecialMobEffects.SLOWNESS.value());
        this.slowness = slowness;
    }

    public float getSlowness() {
        return slowness;
    }

    /**
     * 同一个来源重复施加时保留更强的减速，并沿用新实例上已刷新的持续时间。
     *
     * <p>不同来源之间的比较由 {@link #applyStrongest(LivingEntity)} 负责。
     */
    public Slowness getFinal(Slowness theOld) {
        if (theOld.slowness > slowness) {
            slowness = theOld.slowness;
        }
        return this;
    }

    @Override
    public void onAddOrUpdate(LivingEntity livingEntity) {
        super.onAddOrUpdate(livingEntity);
        applyStrongest(livingEntity);
    }

    @Override
    public void onRemove(LivingEntity livingEntity) {
        super.onRemove(livingEntity);
        applyStrongest(livingEntity);
    }

    /**
     * 目标对原版减速免疫时不再施加。
     *
     * <p>用 {@link CommonHooks#canMobEffectBeApplied} 而不是直接调
     * {@link LivingEntity#canBeAffected(MobEffectInstance)}：前者会 fire
     * {@code MobEffectEvent.Applicable}，事件的默认结果再回落到原版 {@code canBeAffected}，
     * 所以「监听事件否决」和「覆写 canBeAffected」两种免疫方式都能覆盖，
     * 而且避开已弃用的原版方法。注意必须用 3 参数重载，2 参数的那个也已弃用。
     *
     * <p>持续时长取本效果自己的剩余时间，来源实体取本效果的施加者。
     */
    @Override
    public boolean canApplyTo(LivingEntity living) {
        return CommonHooks.canMobEffectBeApplied(living,
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.max(1, getTimeLeft())),
                getSourceEntity(living.level()));
    }

    /**
     * 施加一次减速。
     *
     * @param target 被减速的实体
     * @param source 减速来源，用于区分"不同来源"；同一来源重复施加只会保留更强的那个
     * @param ratio  减速比例，0.3 表示移动速度降低 30%
     * @param ticks  持续时间（tick）
     */
    public static void apply(LivingEntity target, @Nullable Entity source, float ratio, int ticks) {
        Slowness slowness = new Slowness(ratio);
        slowness.setSourceEntity(source);
        slowness.initTime(ticks);
        SpecialEffectAPI.addEffectToEntity(target, slowness, Slowness::getFinal);
    }

    /**
     * 重新计算并施加当前最强的减速。
     *
     * <p>框架保证：新效果放进 map 之后才调用 {@code onAddOrUpdate}，过期效果先从 map 移除
     * 才调用 {@code onRemove}，所以这两个时机读到的都是"变更之后"的完整状态。
     */
    public static void applyStrongest(LivingEntity livingEntity) {
        AttributeInstance speed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        speed.removeModifier(MODIFIER_ID);
        float strongest = getStrongest(livingEntity);
        if (strongest > 0.0F) {
            speed.addTransientModifier(new AttributeModifier(MODIFIER_ID, -strongest, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    /** 当前生效的最强减速比例（0~1），没有任何减速来源时返回 0。 */
    public static float getStrongest(LivingEntity livingEntity) {
        float strongest = 0.0F;
        for (Map<MobEffectType<?>, SpecialMobEffect> effects : SpecialEffectAPI.getEntityEffectData(livingEntity).getEffects().values()) {
            if (effects.get(LRSpecialMobEffects.SLOWNESS.get()) instanceof Slowness slowness) {
                strongest = Math.max(strongest, slowness.getSlowness());
            }
        }
        return strongest;
    }

    /** 图标右下角显示的数字，这里取减速百分比（不含百分号）。 */
    @Override
    public String getString() {
        return String.valueOf(Math.round(slowness * 100.0F));
    }

    @Override
    public CompoundTag save() {
        CompoundTag compoundTag = super.save();
        compoundTag.putFloat("Slowness", slowness);
        return compoundTag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("Slowness")) {
            slowness = compoundTag.getFloat("Slowness");
        }
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(slowness);
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        slowness = buffer.readFloat();
    }
}
