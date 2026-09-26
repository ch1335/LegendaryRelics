package com.chen1335.legendaryRelics.mods.ironsSpellBooks.client;

import com.chen1335.legendaryRelics.mods.ironsSpellBooks.registers.entities.projectiles.SoulMissile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * 灵魂飞弹目前只用粒子表现，所以渲染器刻意留空，只是为了满足实体渲染器注册要求。
 */
public class SoulMissileRenderer extends EntityRenderer<SoulMissile> {
    public SoulMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull SoulMissile entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SoulMissile entity) {
        return MissingTextureAtlasSprite.getLocation();
    }
}
