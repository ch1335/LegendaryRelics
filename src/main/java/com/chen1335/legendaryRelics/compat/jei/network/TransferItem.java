package com.chen1335.legendaryRelics.compat.jei.network;

import com.chen1335.legendaryRelics.API.objects.LRRecipe;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.registers.menus.EquipmentWorkbenchCraftMenu;
import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraft;
import com.chen1335.legendaryRelics.registers.recipes.ingredients.IngredientWithSize;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record TransferItem(ResourceLocation recipeId) implements CustomPacketPayload {
    public static final Type<TransferItem> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LegendaryRelics.MODID, "transfer_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TransferItem> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            TransferItem::recipeId,
            TransferItem::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handler(IPayloadContext context) {
        Player player = context.player();
        RecipeHolder<?> recipeHolder = player.level().getRecipeManager().byName.get(recipeId);
        List<ItemStack> cacheItemStacks = new ArrayList<>();
        List<ItemStack> availableItemStack = new ArrayList<>();
        if (recipeHolder != null) {
            RecipeType<?> type = recipeHolder.value().getType();
            if (type == LRRecipe.EQUIPMENT_CRAFT.value()) {
                EquipmentWorkbenchCraft craft = (EquipmentWorkbenchCraft) recipeHolder.value();
                if (player.containerMenu.getClass() == EquipmentWorkbenchCraftMenu.class) {
                    EquipmentWorkbenchCraftMenu containerMenu = (EquipmentWorkbenchCraftMenu) player.containerMenu;
                    for (int i = 0; i < containerMenu.craftSlots.getContainerSize(); i++) {
                        if (!containerMenu.craftSlots.getItem(i).isEmpty()) {
                            cacheItemStacks.add(containerMenu.craftSlots.getItem(i));
                            containerMenu.craftSlots.setItem(i, ItemStack.EMPTY);
                        }
                    }
                    availableItemStack.addAll(cacheItemStacks);
                    availableItemStack.addAll(player.getInventory().items);

                    NonNullList<IngredientWithSize> require = NonNullList.create();
                    require.add(new IngredientWithSize(craft.mainItem(), 1));
                    require.addAll(craft.secondaryItems());

                    for (ItemStack itemStack : availableItemStack) {
                        for (int i = 0; i < require.size(); i++) {
                            IngredientWithSize ingredientWithSize = require.get(i);
                            if (ingredientWithSize.test(itemStack)) {
                                containerMenu.craftSlots.setItem(i, itemStack.copyWithCount(ingredientWithSize.count()));
                                itemStack.shrink(ingredientWithSize.count());
                                break;
                            }
                        }
                    }

                    for (ItemStack cacheItemStack : cacheItemStacks) {
                        if (!cacheItemStack.isEmpty()) {
                            if (!player.getInventory().add(cacheItemStack)) {
                                player.drop(cacheItemStack, true);
                            }
                        }
                    }
                }
            }


        }
    }
}
