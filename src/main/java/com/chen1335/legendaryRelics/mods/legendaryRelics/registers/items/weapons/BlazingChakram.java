package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.weapons;

import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Add;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.special.DarkGoldUpdateArg;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.Tier;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.items.LRMeleeWeaponBase;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.specialMobEffects.OrbitingFireChakramsEffect;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 烈焰飞轮：命中时召唤环绕自身的飞轮，飞轮自行寻敌突进攻击。
 * <p>
 * 装备效果没有单独注册类型，具体行为直接写在本物品上；环绕调度也由飞轮实体自己完成。
 */
public class BlazingChakram extends LRMeleeWeaponBase {
    /**
     * 生成冷却（秒）：暗金品质从 4 秒降到 3 秒。
     */
    @Calculator
    public static final FinalCalculator GENERATION_COOLDOWN = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(4),
            Constant.of(3)
    ));

    /**
     * 飞轮每秒攻击次数，等于玩家攻速。
     */
    @Calculator
    public static final FinalCalculator ATTACK_INTERVAL = FinalCalculator.of(EntityAttributeValue.of(Attributes.ATTACK_SPEED));

    @Calculator
    public static final FinalCalculator DAMAGE = FinalCalculator.of(Add.of(
            Constant.of(1),
            Mul.of(
                    Constant.of(0.15F),
                    EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
            )
    ));

    @Calculator
    public static final FinalCalculator MAX_CHAKRAM_COUNT = FinalCalculator.of(DarkGoldUpdateArg.of(
            Constant.of(6),
            Constant.of(8)
    ));

    public BlazingChakram() {
        super(Tier.Weapon.BLAZING_CHAKRAM, new Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Level level = context.level();
        if (level != null && level.isClientSide) {
            CalculatorArg arg = CalculatorArg.simpleArg(LRClient.getClientPlayer(), stack);
            CalculatorArg.ArgType.TOOLTIP_FLAG.putArg(arg, tooltipFlag);
            tooltipComponents.add(Component.translatable("item.legendary_relics.blazing_chakram.desc.1").withColor(0xaeaeae));
            tooltipComponents.add(Component.translatable("item.legendary_relics.blazing_chakram.desc.2",
                    GENERATION_COOLDOWN.toComponent(arg)).withColor(0xaeaeae));
            tooltipComponents.add(Component.translatable("item.legendary_relics.blazing_chakram.desc.3",
                    ATTACK_INTERVAL.toComponent(arg)).withColor(0xaeaeae));
            tooltipComponents.add(Component.translatable("item.legendary_relics.blazing_chakram.desc.4",
                    ComponentHolders.Combat.physicalDamage(DAMAGE.toComponent(arg))).withColor(0xaeaeae));
            tooltipComponents.add(Component.translatable("item.legendary_relics.blazing_chakram.desc.5",
                    MAX_CHAKRAM_COUNT.toComponent(arg)).withColor(0xaeaeae));
        }
    }

    /**
     * 近战命中：把最新数值与一次生成请求交给特殊效果，已有实例会被合并（保留飞轮列表与冷却）。
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // hurtEnemy 只在服务端、且命中真正生效时被调用，这里不需要额外判端
        CalculatorArg arg = CalculatorArg.simpleArg(attacker, stack);
        int maxCount = Math.max(0, MAX_CHAKRAM_COUNT.getInt(arg));
        if (maxCount > 0) {
            float damage = Math.max(0.0F, DAMAGE.getValue(arg));
            int attackInterval = Math.max(1, Math.round(20.0F / Math.max(0.01F, ATTACK_INTERVAL.getValue(arg))));
            int generationCooldown = Math.max(1, Math.round(GENERATION_COOLDOWN.getValue(arg) * 20.0F));

            OrbitingFireChakramsEffect effect = new OrbitingFireChakramsEffect();
            effect.setSourceEntity(attacker);
            effect.configure(damage, attackInterval, maxCount, generationCooldown);
            effect.requestGeneration();
            SpecialEffectAPI.addEffectToEntity(attacker, effect, OrbitingFireChakramsEffect::merge);
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}