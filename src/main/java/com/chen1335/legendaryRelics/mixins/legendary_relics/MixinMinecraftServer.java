package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.common.lootInjecter.LootInjectorApplier;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * 数据包 reload 完成后重新应用战利品注入。
 *
 * <p>reload 会把战利品表重新反序列化成全新实例，之前注入的内容全部丢失，所以必须重做一遍。
 * 和 ALI 挂在同一个点（reloadResources 的 TAIL）：priority 比它小就会先应用，
 * 于是我们的 handleAsync 先提交；两边用的是同一个服务端执行器，提交顺序就是执行顺序，
 * 所以 ALI 重读到的仍然是注入后的表。
 */
@Mixin(value = MinecraftServer.class, priority = 900)
public class MixinMinecraftServer {

    @Inject(method = "reloadResources", at = @At("TAIL"))
    private void legendaryRelics$reapplyLootInjectors(Collection<String> packs, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        CompletableFuture<Void> future = cir.getReturnValue();
        if (future == null) {
            return;
        }

        MinecraftServer server = (MinecraftServer) (Object) this;
        future.handleAsync((value, throwable) -> {
            if (throwable == null) {
                LootInjectorApplier.applyAll(server);
            }
            return value;
        }, server);
    }
}
