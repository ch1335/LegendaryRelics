package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.curioEffects;

import com.chen1335.arcanumlib.util.SimpleSchedule;
import com.chen1335.equipmentEffectLib.effectBase.EffectType;
import com.chen1335.equipmentEffectLib.equipmentType.EquipmentType;
import com.chen1335.equipmentEffectLib.utils.CombatUtil;
import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LREquipmentEffectTypes;
import com.chen1335.legendaryRelics.API.objects.LRSounds;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.equipmentEffects.LRBaseEffect;
import com.chen1335.legendaryRelics.network.PlayClientParticlePack;
import com.chen1335.legendaryRelics.utils.LRUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChainLightningEffect extends LRBaseEffect {

    public static final int CHAIN_RANGE = 4;

    public static final int MAX_CHAIN_TARGETS = 5;

    public static final int CHAIN_WAIT_TICKS = 2;

    public static final String PARTICLE_PLAYER_NAME = "chain_lightning";

    @Calculator
    public static final FinalCalculator DAMAGE_RATIO = FinalCalculator.of(Constant.of(0.25F));

    public ChainLightningEffect(EffectType<?> effectType, int level, EquipmentType equipmentType) {
        super(effectType, level, equipmentType);
    }

    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        CalculatorArg arg = CalculatorArg.simpleArg(player, itemStack, this);
        LRUtil.splitAndAdd(tooltipComponents, Component.translatable("equipment_effect.legendary_relics.chain_lightning",
                String.valueOf(CHAIN_RANGE),
                DAMAGE_RATIO.toComponent(tooltipFlag.hasShiftDown(), arg, Style.EMPTY, 100, "%s%%"),
                String.valueOf(MAX_CHAIN_TARGETS)
        ).withColor(0xaeaeae), getMaxToolTipWidth(itemStack));
    }

    public static void LivingDamageEvent(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        Level level = target.level();
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        DamageSource source = event.getSource();
        boolean melee = source.is(DamageTypeTags.IS_PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK);
        boolean ranged = source.is(DamageTypeTags.IS_PROJECTILE);
        if (!melee && !ranged) {
            return;
        }
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker == target) {
            return;
        }
        LREquipmentEffectTypes.CHAIN_LIGHTNING.value().findBestEffect(attacker).ifPresent(slotHolder -> {
            CalculatorArg arg = CalculatorArg.simpleArg(attacker, slotHolder.infoHolder().itemStack(), slotHolder.infoHolder().effect());
            float chainDamage = event.getNewDamage() * DAMAGE_RATIO.getValue(arg);
            if (chainDamage <= 0) {
                return;
            }
            Set<Integer> shockedTargets = new HashSet<>();
            shockedTargets.add(target.getId());
            doChain(serverLevel, attacker, target, chainDamage, shockedTargets, MAX_CHAIN_TARGETS);
        });
    }

    private static void doChain(ServerLevel level, LivingEntity attacker, LivingEntity from, float chainDamage, Set<Integer> shockedTargets, int remainingChains) {
        if (remainingChains <= 0 || attacker.isRemoved()) {
            return;
        }
        LivingEntity target = findNearestTarget(level, attacker, from, shockedTargets);
        if (target == null) {
            return;
        }
        shockedTargets.add(target.getId());
        sendLightningFx(from, target);

        level.playSeededSound(null, from.getX(), from.getY(), from.getZ(), LRSounds.CHAIN_LIGHTNING, SoundSource.PLAYERS, 0.6F, 1, 0);

        target.hurt(level.damageSources().source(LRDamageTypes.CHAIN_LIGHTNING, attacker), chainDamage);
        if (remainingChains > 1) {
            SimpleSchedule.addSchedule(level, new SimpleSchedule.Wait(() ->
                    doChain(level, attacker, target, chainDamage, shockedTargets, remainingChains - 1), CHAIN_WAIT_TICKS));
        }
    }

    @Nullable
    private static LivingEntity findNearestTarget(ServerLevel level, LivingEntity attacker, LivingEntity from, Set<Integer> shockedTargets) {
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, from.getBoundingBox().inflate(CHAIN_RANGE), living ->
                CombatUtil.isAvailableTarget(attacker, living)
                        && !shockedTargets.contains(living.getId())
                        && from.distanceTo(living) <= CHAIN_RANGE
        );
        LivingEntity nearest = null;
        double nearestDistanceSqr = Double.MAX_VALUE;
        for (LivingEntity candidate : candidates) {
            double distanceSqr = from.distanceToSqr(candidate);
            if (distanceSqr < nearestDistanceSqr) {
                nearestDistanceSqr = distanceSqr;
                nearest = candidate;
            }
        }
        return nearest;
    }

    private static void sendLightningFx(LivingEntity from, LivingEntity to) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("fromId", from.getId());
        tag.putInt("toId", to.getId());
        Vec3 fromPos = boltAnchor(from);
        Vec3 toPos = boltAnchor(to);
        tag.putDouble("fromX", fromPos.x);
        tag.putDouble("fromY", fromPos.y);
        tag.putDouble("fromZ", fromPos.z);
        tag.putDouble("toX", toPos.x);
        tag.putDouble("toY", toPos.y);
        tag.putDouble("toZ", toPos.z);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(from, new PlayClientParticlePack(PARTICLE_PLAYER_NAME, tag));
    }

    private static Vec3 boltAnchor(Entity entity) {
        return entity.position().add(0, entity.getBbHeight() * 0.5, 0);
    }

    public static void playClientParticle(Level level, CompoundTag tag) {
        drawBolt(level, tag);
        for (int tick = 1; tick < CHAIN_WAIT_TICKS; tick++) {
            SimpleSchedule.addSchedule(level, new SimpleSchedule.Wait(() -> drawBolt(level, tag), tick));
        }
    }

    private static void drawBolt(Level level, CompoundTag tag) {
        Vec3 fromPos = resolveAnchor(level, tag.getInt("fromId"), tag.getDouble("fromX"), tag.getDouble("fromY"), tag.getDouble("fromZ"));
        Vec3 toPos = resolveAnchor(level, tag.getInt("toId"), tag.getDouble("toX"), tag.getDouble("toY"), tag.getDouble("toZ"));
        RandomSource random = level.random;
        int segments = 6;
        Vec3[] points = new Vec3[segments + 1];
        points[0] = fromPos;
        points[segments] = toPos;
        for (int i = 1; i < segments; i++) {
            double t = (double) i / segments;
            double jitter = 0.35;
            points[i] = fromPos.lerp(toPos, t).add(
                    (random.nextDouble() - 0.5) * 2 * jitter,
                    (random.nextDouble() - 0.5) * 2 * jitter,
                    (random.nextDouble() - 0.5) * 2 * jitter
            );
        }
        for (int i = 0; i < segments; i++) {
            Vec3 start = points[i];
            Vec3 end = points[i + 1];
            double length = start.distanceTo(end);
            int steps = Math.max(1, (int) (length / 0.15));
            for (int step = 0; step <= steps; step++) {
                Vec3 pos = start.lerp(end, (double) step / steps);
                level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 0, 0, 0);
            }
        }
    }

    private static Vec3 resolveAnchor(Level level, int entityId, double x, double y, double z) {
        Entity entity = level.getEntity(entityId);
        if (entity != null && entity.isAlive()) {
            return boltAnchor(entity);
        }
        return new Vec3(x, y, z);
    }
}
