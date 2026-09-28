package com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.specialMobEffects;

import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.ISSRegisters;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.TimeLimitEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * 法术抑制：携带者造成的法术伤害降低。
 *
 * <p>本身没有任何 tick 行为，只作为一个"减伤比例 + 剩余时间"的载体，
 * 实际减免在 SpellDamageEvent 里读取。
 */
public class MagicSuppression extends TimeLimitEffect {

    /** 默认减伤比例，0.3 表示降低 30%。 */
    public static final float DEFAULT_DAMAGE_REDUCTION = 0.3F;

    private float damageReduction = DEFAULT_DAMAGE_REDUCTION;

    public MagicSuppression(MobEffectType<?> effectType) {
        super(effectType);
    }

    public MagicSuppression(float damageReduction) {
        this(ISSRegisters.SpecialMobEffect.MAGIC_SUPPRESSION.value());
        this.damageReduction = damageReduction;
    }

    public float getDamageReduction() {
        return damageReduction;
    }

    /**
     * 重复施加时保留更强的减伤，并沿用新实例上已刷新的持续时间。
     */
    public MagicSuppression getFinal(MagicSuppression theOld) {
        if (theOld.damageReduction > damageReduction) {
            damageReduction = theOld.damageReduction;
        }
        return this;
    }

    @Override
    public CompoundTag save() {
        CompoundTag compoundTag = super.save();
        compoundTag.putFloat("DamageReduction", damageReduction);
        return compoundTag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("DamageReduction")) {
            damageReduction = compoundTag.getFloat("DamageReduction");
        }
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeFloat(damageReduction);
    }

    @Override
    public void decode(@NotNull RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        damageReduction = buffer.readFloat();
    }
}
