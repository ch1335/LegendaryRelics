package com.chen1335.legendaryRelics.client.particles;

import com.chen1335.legendaryRelics.common.particles.WaveIceParticleOptions;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * 冰波粒子：世界空间固定朝向（平面垂直于冲刺方向），逐渐变大并淡出消失。
 * <p>贴图 48x48，最终铺满 3x3 格（48 / 16）。
 */
public class WaveIceParticle extends Particle {
    /** 生命周期（tick）。 */
    private static final int LIFETIME = 20;
    /** 生成时的半宽（格）：0.5 → 1 格宽。 */
    private static final float START_SIZE = 0.5F;
    /** 结束时的半宽（格）：1.5 → 3 格宽。 */
    private static final float END_SIZE = 1.5F;

    private final TextureAtlasSprite sprite;
    private final float startAlpha;
    /** 水平单位方向（冲刺方向），决定平面的朝向。 */
    private final float dirX;
    private final float dirZ;
    private float size = START_SIZE;
    /** 上一 tick 的尺寸，渲染时和 size 按 partialTick 插值。 */
    private float sizeO = START_SIZE;

    public WaveIceParticle(ClientLevel level, double x, double y, double z, WaveIceParticleOptions options, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprite = sprites.get(this.random);
        this.lifetime = LIFETIME;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.xd = this.yd = this.zd = 0.0;

        this.startAlpha = Mth.clamp(options.startAlpha(), 0.0F, 1.0F);
        this.alpha = this.startAlpha;

        // options.rollDegrees 在这里当作 yaw 用，转成水平方向向量
        double yaw = Math.toRadians(options.rollDegrees());
        this.dirX = (float) -Math.sin(yaw);
        this.dirZ = (float) Math.cos(yaw);
    }

    @Override
    public void tick() {
        super.tick();
        float progress = Mth.clamp(((float) this.age / (float) this.lifetime)*2, 0.0F, 1.0F);
        // 逐渐变大
        this.sizeO = this.size;
        this.size = Mth.lerp(progress, START_SIZE, END_SIZE);
        // 从当前透明度淡出到 0
        this.alpha = Mth.lerp(progress, this.startAlpha, 0.0F);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        float cx = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float cy = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
        float cz = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());
        // 世界空间侧向轴：与冲刺方向垂直
        float sideX = -this.dirZ;
        float sideZ = this.dirX;

        float u0 = this.sprite.getU0();
        float u1 = this.sprite.getU1();
        float v0 = this.sprite.getV0();
        float v1 = this.sprite.getV1();
        int light = this.getLightColor(partialTick);
        // 尺寸按 partialTick 插值，避免逐 tick 跳变
        float renderSize = Mth.lerp(partialTick, this.sizeO, this.size);

        // 正面
        this.quad(buffer, cx, cy, cz, sideX, sideZ, renderSize, u0, u1, v0, v1, light,
                -1.0F, -1.0F, u0, v1,
                1.0F, -1.0F, u1, v1,
                1.0F, 1.0F, u1, v0,
                -1.0F, 1.0F, u0, v0);
        // 背面，避免从另一侧看时透明
        this.quad(buffer, cx, cy, cz, sideX, sideZ, renderSize, u0, u1, v0, v1, light,
                1.0F, -1.0F, u1, v1,
                -1.0F, -1.0F, u0, v1,
                -1.0F, 1.0F, u0, v0,
                1.0F, 1.0F, u1, v0);
    }

    private void quad(VertexConsumer buffer, float cx, float cy, float cz, float sideX, float sideZ, float renderSize,
                      float u0, float u1, float v0, float v1, int light,
                      float x0, float y0, float uA, float vA,
                      float x1, float y1, float uB, float vB,
                      float x2, float y2, float uC, float vC,
                      float x3, float y3, float uD, float vD) {
        this.vertex(buffer, cx, cy, cz, sideX, sideZ, renderSize, x0, y0, uA, vA, light);
        this.vertex(buffer, cx, cy, cz, sideX, sideZ, renderSize, x1, y1, uB, vB, light);
        this.vertex(buffer, cx, cy, cz, sideX, sideZ, renderSize, x2, y2, uC, vC, light);
        this.vertex(buffer, cx, cy, cz, sideX, sideZ, renderSize, x3, y3, uD, vD, light);
    }

    private void vertex(VertexConsumer buffer, float cx, float cy, float cz, float sideX, float sideZ, float renderSize,
                        float sideOffset, float upOffset, float u, float v, int light) {
        buffer.addVertex(
                        cx + sideX * sideOffset * renderSize,
                        cy + upOffset * renderSize,
                        cz + sideZ * sideOffset * renderSize)
                .setUv(u, v)
                .setColor(this.rCol, this.gCol, this.bCol, this.alpha)
                .setLight(light);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public AABB getRenderBoundingBox(float partialTicks) {
        double r = END_SIZE;
        return new AABB(this.x - r, this.y - r, this.z - r, this.x + r, this.y + r, this.z + r);
    }

    @Override
    public int getLightColor(float partialTick) {
        // 全亮，避免在洞穴/夜里发黑
        return 15728880;
    }
}
