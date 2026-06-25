package com.chen1335.legendaryRelics.registers.armorSetEffect;

import com.chen1335.equipmentEffectLib.attachmentDatas.EntitySetsEffectData;
import com.chen1335.equipmentEffectLib.common.EffectInstance;
import com.chen1335.equipmentEffectLib.equipmentSetEffect.SetEffect;
import com.chen1335.legendaryRelics.API.objects.LRDamageTypes;
import com.chen1335.legendaryRelics.API.objects.LRSetsEffects;
import com.chen1335.legendaryRelics.API.objects.LRSpecialMobEffect;
import com.chen1335.legendaryRelics.client.LRClient;
import com.chen1335.legendaryRelics.client.particlePlayer.ParticlePlayersHolder;
import com.chen1335.legendaryRelics.common.ComponentHolders;
import com.chen1335.legendaryRelics.common.calculator.CalculatorArg;
import com.chen1335.legendaryRelics.common.calculator.FinalCalculator;
import com.chen1335.legendaryRelics.common.calculator.annotations.Calculator;
import com.chen1335.legendaryRelics.common.calculator.normal.Constant;
import com.chen1335.legendaryRelics.common.calculator.normal.Mul;
import com.chen1335.legendaryRelics.common.calculator.normal.MultiAdd;
import com.chen1335.legendaryRelics.common.calculator.special.EntityAttributeValue;
import com.chen1335.legendaryRelics.common.calculator.special.TieredBonus;
import com.chen1335.legendaryRelics.effectInstances.InfernoEffectInstance;
import com.chen1335.legendaryRelics.registers.specialMobEffects.InfernoScorch;
import com.chen1335.legendaryRelics.utils.LRColors;
import com.chen1335.legendaryRelics.utils.SimpleSchedule;
import com.chen1335.specialEffectLib.API.SpecialEffectAPI;
import com.chen1335.specialEffectLib.mobEffect.MobEffectType;
import com.chen1335.specialEffectLib.mobEffect.SpecialMobEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class InfernoArmorSetEffect extends SetEffect {

    @Calculator
    public static final FinalCalculator GAIN_STACK_COOLDOWN = FinalCalculator.of(
            TieredBonus.of(List.of(1.5F, 1.5F, 0.75F, 0.5F))
    );
    @Calculator
    public static final FinalCalculator ATTACK_RANGE_PER_STACK = FinalCalculator.of(
            TieredBonus.of(List.of(0.05F, 0.05F, 0.1F, 0.1F))
    );
    @Calculator
    public static final FinalCalculator DAMAGE_PER_STACK = FinalCalculator.of(
            TieredBonus.of(List.of(0.03F, 0.03F, 0.04F, 0.05F))
    );
    @Calculator
    public static final FinalCalculator KEEP_TIME = FinalCalculator.of(
            TieredBonus.of(List.of(4F, 4F, 7F, 10F))
    );

    @Calculator
    public static final FinalCalculator INFERNO_SCORCH_DAMAGE = FinalCalculator.of(
            Mul.of(
                    MultiAdd.of(
                            Constant.of(2F),
                            Mul.of(
                                    Constant.of(0.4F),
                                    EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
                            )
                    ),
                    TieredBonus.of(List.of(0.64F, 0.64F, 0.81F, 1F))
            )
    );

    @Calculator
    public static final FinalCalculator INFERNO_EXPLOSION_DAMAGE = FinalCalculator.of(
            Mul.of(
                    MultiAdd.of(
                            Constant.of(5F),
                            EntityAttributeValue.of(Attributes.ATTACK_DAMAGE)
                    ),
                    TieredBonus.of(List.of(0.64F, 0.64F, 0.81F, 1F))
            )
    );


    @Override
    public void appendToolTip(ItemStack itemStack, Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, List<Component> list) {
        CalculatorArg args = buildArg(player);
        boolean shiftDown = tooltipFlag.hasShiftDown();
        list.add(Component.translatable("set_effect_type.legendary_relics.tiered_bonus", Component.translatable("set_effect.legendary_relics.inferno.name").append("(%s/4)".formatted(getPiece(player))).withColor(16733695)).withColor(16755200));
        list.add(Component.translatable("set_effect.legendary_relics.inferno.desc.1",
                GAIN_STACK_COOLDOWN.toComponent(shiftDown, args, LRColors.Component.YELLOW.getColor()),
                doomComponent()
        ).withStyle(ChatFormatting.GRAY));


        list.add(Component.translatable("set_effect.legendary_relics.inferno.desc.2",
                KEEP_TIME.toComponent(shiftDown, args),
                doomComponent(),
                doomComponent()
        ).withStyle(ChatFormatting.GRAY));


        list.add(Component.translatable("set_effect.legendary_relics.inferno.desc.3",
                ATTACK_RANGE_PER_STACK.toComponent(shiftDown, args, LRColors.Component.BLUE.getColor()),
                DAMAGE_PER_STACK.toPercentageComponent(shiftDown, args, LRColors.Component.BLUE.getColor())
        ).withStyle(ChatFormatting.GRAY));


        list.add(Component.translatable("set_effect.legendary_relics.inferno.desc.4",
                ComponentHolders.Effects.infernoScorch(),
                ComponentHolders.Combat.physicalDamage(INFERNO_SCORCH_DAMAGE.toComponent(shiftDown, args, LRColors.Component.RED.getColor()))
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.inferno.desc.5",
                ComponentHolders.Effects.infernoScorch(),
                ComponentHolders.Effects.infernoScorch(),
                ComponentHolders.Combat.physicalDamage(INFERNO_EXPLOSION_DAMAGE.toComponent(shiftDown, args, LRColors.Component.RED.getColor()))
        ).withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("set_effect.legendary_relics.armor_pieces_required", "2+"
        ).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static Component doomComponent() {
        return Component.empty().append("").append(Component.translatable("legendary_relics.stack.doom").withStyle(ChatFormatting.DARK_RED));
    }

    @Override
    public EffectInstance createInstance(int piece) {
        return new InfernoEffectInstance(piece);
    }

    public static InfernoEffectInstance getEffectInstance(LivingEntity living) {
        return EntitySetsEffectData.getSetInstance(living, LRSetsEffects.INFERNO_ARMOR.value(), InfernoEffectInstance.class);
    }

    public static void LivingIncomingDamageEvent(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if ((source.is(DamageTypeTags.IS_PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK)) && source.is(Tags.DamageTypes.IS_PHYSICAL) && source.getEntity() instanceof LivingEntity attacker) {
            InfernoEffectInstance instance = getEffectInstance(attacker);
            if (instance != null) {
                CalculatorArg args = CalculatorArg.simpleArg(attacker);
                int piece = LRClient.ENTITY_SETS_EFFECT_DATA.getOrDefault(LRSetsEffects.INFERNO_ARMOR.value(), 0);
                args.putArg(TieredBonus.TIER, piece);
                if (instance.coolDown <= 0) {
                    instance.addStack(1);
                    float cooldownValueSecond = GAIN_STACK_COOLDOWN.getValue(args);
                    instance.coolDown = (int) (cooldownValueSecond * 20);
                    instance.keepTime = (int) (KEEP_TIME.getValue(args) * 20);
                }
                if (instance.stack >= 10) {
                    InfernoScorch infernoScorch = new InfernoScorch(INFERNO_SCORCH_DAMAGE.getValue(args), INFERNO_EXPLOSION_DAMAGE.getValue(args));
                    infernoScorch.setSourceEntity(attacker);
                    SpecialEffectAPI.addEffectToEntity(event.getEntity(), infernoScorch, InfernoScorch::getFinal);
                }
            }
        }
    }

    public static void LivingDeathEvent(LivingDeathEvent event) {
        if (!event.getEntity().level().isClientSide) {
            Level level = event.getEntity().level();
            SimpleSchedule.addSchedule(level, new SimpleSchedule.Wait(() -> {
                LivingEntity entity = event.getEntity();
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putInt("id", entity.getId());
                Map<UUID, Map<MobEffectType<?>, SpecialMobEffect>> effects = SpecialEffectAPI.getEntityEffectData(event.getEntity()).getEffects();

                List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(event.getEntity().position(), 8, 8, 8), living -> living != entity);

                boolean flag = false;
                for (Map<MobEffectType<?>, SpecialMobEffect> value : effects.values()) {
                    InfernoScorch effect = (InfernoScorch) value.get(LRSpecialMobEffect.INFERNO_SCORCH.value());
                    if (effect != null) {
                        Entity sourceEntity = effect.getSourceEntity(level);
                        for (LivingEntity nearbyEntity : nearbyEntities) {
                            if (sourceEntity != null && !nearbyEntity.isAlliedTo(sourceEntity) && nearbyEntity != sourceEntity) {
                                InfernoScorch infernoScorch = new InfernoScorch(effect.getDamage(), effect.getExplosionDamage());
                                infernoScorch.setSourceEntity(effect.getSourceEntity(level));
                                SpecialEffectAPI.addEffectToEntity(nearbyEntity, infernoScorch, InfernoScorch::getFinal);
                                nearbyEntity.hurt(level.damageSources().source(LRDamageTypes.INFERNO_SCORCH, effect.getSourceEntity(level)), effect.getExplosionDamage());
                            }
                        }
                        flag = true;
                    }
                }
                if (flag) {
                    ParticlePlayersHolder.sendToPlayersTrackingEntity(entity, "inferno_explosion", compoundTag);
                    level.playSound(null,entity.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS,0.5F,1F);
                }
            }, 5));
        }
    }

    public static void playClientParticle(Level level, CompoundTag compoundTag) {
        int id = compoundTag.getInt("id");
        Entity entity = level.getEntity(id);
        if (entity != null) {
            for (int i = 0; i < 90; i++) {
                float x = Mth.cos(i);
                float z = Mth.sin(i);
                float mul = 0.18F;
                level.addParticle(ParticleTypes.FLAME, entity.getX(), entity.getY() + entity.getEyeHeight() / 2, entity.getZ(), x * mul, 0, z * mul);
            }
        }
    }
}
