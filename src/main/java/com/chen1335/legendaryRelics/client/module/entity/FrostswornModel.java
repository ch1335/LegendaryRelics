package com.chen1335.legendaryRelics.client.module.entity;

import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.mods.legendaryRelics.registers.entities.monsters.Frostsworn;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

/** GeckoLib 的资源定位是 assets/&lt;命名空间&gt;/ 下的绝对路径，不额外拼前缀。 */
public class FrostswornModel extends GeoModel<Frostsworn> {

    private static final ResourceLocation MODEL = LegendaryRelics.id("geo/frostsworn.geo.json");
    private static final ResourceLocation TEXTURE = LegendaryRelics.id("textures/entity/frostsworn.png");
    private static final ResourceLocation ANIMATION = LegendaryRelics.id("animations/frostsworn.animation.json");

    private static final String HEAD_BONE = "head";

    @Override
    public ResourceLocation getModelResource(Frostsworn animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Frostsworn animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Frostsworn animatable) {
        return ANIMATION;
    }

    /**
     * 只做头部跟随。
     *
     * <p>移动与攻击动画完全由动画文件决定，这里不按移速缩放摆幅，也不叠加走路腿。
     */
    @Override
    public void setCustomAnimations(Frostsworn animatable, long instanceId, AnimationState<Frostsworn> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        // 动画自己写的 head 关键帧会被跟随覆盖，对本生物来说这是想要的效果。
        this.applyHeadRotation(animatable, animationState);
    }

    /**
     * 头部跟随视线。
     *
     * <p>GeckoLib 不会自动把 yHeadRot/xRot 应用到骨骼上，所以 LookAtPlayerGoal 这类原版目标
     * 对模型本来是不可见的，必须自己推。
     *
     * <p>三个坑：{@code .O} 字段每 tick 才更新，必须跨 tick 插值否则 20Hz 抖；
     * yHeadRot 是世界朝向，要减掉 yBodyRot（骨骼已在整体朝向下）；
     * 符号上：骨骼 X 轴与 getXRot 相反，Y 轴与 yaw 也相反（Bedrock 骨骼的 Y 是逆时针为正，Minecraft 的 yaw 是顺时针为正）。
     *
     * <p>这里必须用「静止姿势 + 视线」而不是「当前值 + 视线」：动画没给 head 写关键帧时
     * GeckoLib 不会每帧重置这根骨骼（要等 getBoneResetTime 才复位），在当前值上累加会逐帧叠加、
     * 表现为头部不停旋转。代价是动画自带的 head 关键帧会被跟随覆盖，对本生物来说这正是想要的。
     */
    private void applyHeadRotation(Frostsworn animatable, AnimationState<Frostsworn> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone(HEAD_BONE);
        if (head == null) {
            return;
        }

        float partial = animationState.getPartialTick();
        float pitch = Mth.lerp(partial, animatable.xRotO, animatable.getXRot());
        float yaw = Mth.lerp(partial,
                Mth.wrapDegrees(animatable.yHeadRotO - animatable.yBodyRotO),
                Mth.wrapDegrees(animatable.yHeadRot - animatable.yBodyRot));

        BoneSnapshot rest = head.getInitialSnapshot();
        head.updateRotation(
                rest.getRotX() - pitch * Mth.DEG_TO_RAD,
                rest.getRotY() - yaw * Mth.DEG_TO_RAD,
                rest.getRotZ());
    }
}
