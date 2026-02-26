package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.common.lootModifier.lootInject.LootInjectors;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {
    @Inject(method = "reload",at = @At("HEAD"))
    private static void onLoad(LayeredRegistryAccess<RegistryLayer> registries, ResourceManager resourceManager, Executor backgroundExecutor, CallbackInfoReturnable<CompletableFuture<LayeredRegistryAccess<RegistryLayer>>> cir){
        LootInjectors.INJECTORS.clear();
        LootInjectors.load();
    }
}
