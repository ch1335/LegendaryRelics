package com.chen1335.legendaryRelics.mixins.main;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererInvoker {
    @Invoker("getFov")
    double lr$getFov(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting);
}
