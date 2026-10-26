package com.chen1335.legendaryRelics.mods.legendaryRelics.registers.attachmentDatas;

import com.chen1335.arcanumlib.modules.attribute.events.LivingGetAttributeAdjustmentEvent;
import com.chen1335.legendaryRelics.API.objects.LRAttachmentTypes;
import com.chen1335.legendaryRelics.common.EquipmentEffectCooldownManager;
import com.chen1335.legendaryRelics.common.TimeLimitedAttributeBonusManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LREntityData implements INBTSerializable<CompoundTag> {
    public int reaperPickCooldown = 0;

    private final TimeLimitedAttributeBonusManager timeLimitedAttributeBonusManager = new TimeLimitedAttributeBonusManager();

    private final EquipmentEffectCooldownManager equipmentEffectCooldownManager = new EquipmentEffectCooldownManager();

    /** 神圣斗篷的飘扬状态。纯客户端渲染数据，不参与 NBT 序列化。 */
    public final CloakRenderData cloakRender = new CloakRenderData();


    private final Map<Object, Consumer<LivingGetAttributeAdjustmentEvent>> map = new HashMap<>();

    public void addAdjustmentConsumer(Object o, Consumer<LivingGetAttributeAdjustmentEvent> consumer) {
        map.put(o, consumer);
    }

    public void removeAdjustmentConsumer(Object o) {
        map.remove(o);
    }

    public void applyAdjustment(Object o, LivingGetAttributeAdjustmentEvent event) {
        Consumer<LivingGetAttributeAdjustmentEvent> consumer = map.get(o);
        if (consumer != null) {
            consumer.accept(event);
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("AttributeBonusData", timeLimitedAttributeBonusManager.serializeNBT(provider));
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        timeLimitedAttributeBonusManager.deserializeNBT(provider, nbt.getCompound("AttributeBonusData"));
    }

    public void tick(LivingEntity living) {
        timeLimitedAttributeBonusManager.tick(living);
        equipmentEffectCooldownManager.tick(living);
        reaperPickCooldown = Math.max(0, reaperPickCooldown - 1);
    }

    public TimeLimitedAttributeBonusManager getTimeLimitedAttributeBonusManager() {
        return timeLimitedAttributeBonusManager;
    }

    public EquipmentEffectCooldownManager getEquipmentEffectCooldownManager() {
        return equipmentEffectCooldownManager;
    }

    public static LREntityData getData(LivingEntity living) {
        return living.getData(LRAttachmentTypes.ENTITY_DATA.get());
    }

    /**
     * 披风的飘扬状态，逻辑照搬原版披风（{@code Player#moveCloak} 与 {@code CapeLayer}）。
     *
     * <p>位置是一阶滞后跟随（每 tick 走 25% 的剩余距离），渲染时再按这个滞后的位移
     * 反推倾角，于是加速、急停、上下浮动都会自然带动披风。
     */
    public static class CloakRenderData {
        private double x;
        private double y;
        private double z;
        private double xo;
        private double yo;
        private double zo;
        private int lastTick = Integer.MIN_VALUE;
        private boolean initialized = false;

        /**
         * 推进一 tick 的位置跟随。
         *
         * <p>渲染帧率高于 tick 率，同一 tick 内重复调用会被直接忽略。
         */
        public void tick(LivingEntity living) {
            if (lastTick == living.tickCount) {
                return;
            }
            lastTick = living.tickCount;

            if (!initialized) {
                initialized = true;
                x = xo = living.getX();
                y = yo = living.getY();
                z = zo = living.getZ();
                return;
            }

            xo = x;
            yo = y;
            zo = z;

            double dx = living.getX() - x;
            double dy = living.getY() - y;
            double dz = living.getZ() - z;

            // 单轴位移超过 10 格（传送、重生等）直接吸附，避免披风被甩飞
            if (dx > 10.0 || dx < -10.0) {
                xo = x = living.getX();
            }
            if (dy > 10.0 || dy < -10.0) {
                yo = y = living.getY();
            }
            if (dz > 10.0 || dz < -10.0) {
                zo = z = living.getZ();
            }

            x += dx * 0.25;
            y += dy * 0.25;
            z += dz * 0.25;
        }

        /**
         * 披风绕 X 轴的倾角（弧度），等价于原版披风渲染施加在 X 轴上的那个旋转。
         *
         * <p>原版把总计 {@code 6 + f2/2 + f1} 度施加在 PoseStack 上；这里直接写进
         * 部件的 {@code xRot}，因此旋转中心是部件自身的轴心（颈后），比原版绕 entity
         * 原点旋转更贴近"披风挂在肩上"的观感。
         */
        public float getXRot(LivingEntity living, float partialTicks) {
            double dx = Mth.lerp(partialTicks, xo, x) - Mth.lerp(partialTicks, living.xo, living.getX());
            double dy = Mth.lerp(partialTicks, yo, y) - Mth.lerp(partialTicks, living.yo, living.getY());
            double dz = Mth.lerp(partialTicks, zo, z) - Mth.lerp(partialTicks, living.zo, living.getZ());

            float bodyRot = Mth.rotLerp(partialTicks, living.yBodyRotO, living.yBodyRot);
            double sin = Mth.sin(bodyRot * ((float) Math.PI / 180));
            double cos = -Mth.cos(bodyRot * ((float) Math.PI / 180));

            float f1 = Mth.clamp((float) dy * 10.0F, -6.0F, 32.0F);
            float f2 = Mth.clamp((float) (dx * sin + dz * cos) * 100.0F, 0.0F, 150.0F);

            // bob 只存在于 Player 上，非玩家实体跳过这一项（原版的走路摆动）
            if (living instanceof Player player) {
                float bob = Mth.lerp(partialTicks, player.oBob, player.bob);
                f1 += Mth.sin(Mth.lerp(partialTicks, player.walkDistO, player.walkDist) * 6.0F) * 32.0F * bob;
            }
            if (living.isCrouching()) {
                f1 += 25.0F;
            }

            return (float) Math.toRadians(6.0F + f2 / 2.0F + f1);
        }
    }
}
