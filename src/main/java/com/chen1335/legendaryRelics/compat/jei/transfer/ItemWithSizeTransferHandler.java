package com.chen1335.legendaryRelics.compat.jei.transfer;

import com.chen1335.legendaryRelics.API.objects.LRMenus;
import com.chen1335.legendaryRelics.API.objects.LRRecipe;
import com.chen1335.legendaryRelics.compat.jei.LRJeiPlugin;
import com.chen1335.legendaryRelics.compat.jei.network.TransferItem;
import com.chen1335.legendaryRelics.registers.menus.EquipmentWorkbenchCraftMenu;
import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraft;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.transfer.IUniversalRecipeTransferHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.util.Cast;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ItemWithSizeTransferHandler implements IUniversalRecipeTransferHandler<EquipmentWorkbenchCraftMenu> {

    private final IJeiHelpers jeiHelpers;
    private final IRecipeTransferHandlerHelper transferHelper;

    public ItemWithSizeTransferHandler(IJeiHelpers jeiHelpers, IRecipeTransferHandlerHelper transferHelper) {
        this.jeiHelpers = jeiHelpers;
        this.transferHelper = transferHelper;
    }

    @Override
    public Class<? extends EquipmentWorkbenchCraftMenu> getContainerClass() {
        return EquipmentWorkbenchCraftMenu.class;
    }

    @Override
    public Optional<MenuType<EquipmentWorkbenchCraftMenu>> getMenuType() {

        return Optional.of(LRMenus.EQUIPMENT_WORKBENCH_CRAFT.value());
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(EquipmentWorkbenchCraftMenu container, Object object, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer) {
        if (!transferHelper.recipeTransferHasServerSupport()) {
            Component tooltipMessage = Component.translatable("jei.tooltip.error.recipe.transfer.no.server");
            return transferHelper.createUserErrorWithTooltip(tooltipMessage);
        }


        if (!(object instanceof RecipeHolder<?> holder1 && holder1.value().getType() == LRRecipe.EQUIPMENT_CRAFT.value())) {
            return transferHelper.createInternalError();
        }
        RecipeHolder<EquipmentWorkbenchCraft> holder = Cast.cast(holder1);
        IRecipeTransferInfo<EquipmentWorkbenchCraftMenu, RecipeHolder<EquipmentWorkbenchCraft>> transferInfo = transferHelper.createBasicRecipeTransferInfo(EquipmentWorkbenchCraftMenu.class, LRMenus.EQUIPMENT_WORKBENCH_CRAFT.value(), LRJeiPlugin.EQUIPMENT_CRAFT, 1, 9, 10, 36);

        List<Slot> craftingSlots = Collections.unmodifiableList(transferInfo.getRecipeSlots(container, holder));
        List<Slot> inventorySlots = Collections.unmodifiableList(transferInfo.getInventorySlots(container, holder));


        List<ItemStack> availableItemStacks = new ArrayList<>();
        craftingSlots.forEach(slot -> {
            if (!slot.getItem().isEmpty()) {
                availableItemStacks.add(slot.getItem());
            }
        });

        inventorySlots.forEach(slot -> {
            if (!slot.getItem().isEmpty()) {
                availableItemStacks.add(slot.getItem());
            }
        });

        List<IRecipeSlotView> missing = new ArrayList<>();

        List<IRecipeSlotView> inputItemSlotViews = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT);

        for (IRecipeSlotView inputItemSlotView : inputItemSlotViews) {
            for (ItemStack itemStack : inputItemSlotView.getItemStacks().toList()) {
                boolean hasRequireItems = false;
                for (ItemStack availableItemStack : availableItemStacks) {
                    if (itemStack.is(availableItemStack.getItem()) && availableItemStack.getCount() >= itemStack.getCount()) {
                        hasRequireItems = true;
                        break;
                    }
                }
                if (!hasRequireItems) {
                    missing.add(inputItemSlotView);
                }
            }
        }

        if (!missing.isEmpty() && !player.isCreative()) {
            Component message = Component.translatable("jei.tooltip.error.recipe.transfer.missing");
            return transferHelper.createUserErrorForMissingSlots(message, missing);
        }


        if (doTransfer) {
            RecipeType<?> type = holder.value().getType();
            for (RecipeHolder<?> recipeHolder : player.level().getRecipeManager().getAllRecipesFor(Cast.cast(type))) {
                if (recipeHolder.value() == holder.value()) {
                    PacketDistributor.sendToServer(new TransferItem(recipeHolder.id()));
                    break;
                }
            }

        }
        return null;
    }
}
