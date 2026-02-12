package com.chen1335.legendaryRelics.mixins.legendary_relics;

import com.chen1335.legendaryRelics.equipmentEffects.curioEffects.GameTaskEffect;
import com.chen1335.legendaryRelics.misc.gameTask.taskTypes.GainItemTask;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method = "initMenu", at = @At("RETURN"))
    private void addSlotListener(AbstractContainerMenu menu, CallbackInfo ci) {
        Inventory inventory = this.getInventory();
        ServerPlayer serverPlayer = ((ServerPlayer) (Object) this);
        menu.addSlotListener(new ContainerListener() {
            @Override
            public void slotChanged(@NotNull AbstractContainerMenu menu1, int dataSlotIndex, @NotNull ItemStack itemStack) {
                Slot slot = menu1.getSlot(dataSlotIndex);
                if (!(slot instanceof ResultSlot)) {
                    if (slot.container == inventory) {
                        GameTaskEffect.tryFinishTask(serverPlayer, GainItemTask.of(itemStack.getItem()));
                    }
                }
            }

            @Override
            public void dataChanged(@NotNull AbstractContainerMenu menu1, int p_143463_, int p_143464_) {
            }
        });
    }
}
