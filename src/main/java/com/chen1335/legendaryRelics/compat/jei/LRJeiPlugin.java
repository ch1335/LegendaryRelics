package com.chen1335.legendaryRelics.compat.jei;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.API.objects.LRRecipe;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.compat.jei.recipeCategory.EquipmentCraftCategory;
import com.chen1335.legendaryRelics.compat.jei.transfer.ItemWithSizeTransferHandler;
import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraft;
import com.chen1335.legendaryRelics.registers.screens.EquipmentWorkbenchCraftScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.*;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import com.chen1335.equipmentEffectLib.utils.Cast;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@JeiPlugin
public class LRJeiPlugin implements IModPlugin {
    public static final RecipeType<RecipeHolder<EquipmentWorkbenchCraft>> EQUIPMENT_CRAFT = RecipeType.create(LegendaryRelics.MODID, "equipment_craft", Cast.cast(RecipeHolder.class));


    @Override
    public ResourceLocation getPluginUid() {
        return LegendaryRelics.id("jei");
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;

        if (level != null) {
            Stream<RecipeHolder<EquipmentWorkbenchCraft>> stream = level.getRecipeManager().getAllRecipesFor(LRRecipe.EQUIPMENT_CRAFT.value()).stream();
            registration.addRecipes(EQUIPMENT_CRAFT, stream.toList());
        }
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(EQUIPMENT_CRAFT, LRItems.EQUIPMENT_WORKBENCH);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new EquipmentCraftCategory(guiHelper));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(EquipmentWorkbenchCraftScreen.class, 118, 56, 20, 15, EQUIPMENT_CRAFT);
    }


    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IRecipeTransferHandlerHelper transferHelper = registration.getTransferHelper();
        ItemWithSizeTransferHandler itemWithSizeTransferHandler = new ItemWithSizeTransferHandler(jeiHelpers, transferHelper);
        registration.addUniversalRecipeTransferHandler(itemWithSizeTransferHandler);
//        registration.addRecipeTransferHandler(EquipmentWorkbenchCraftMenu.class, LRMenus.EQUIPMENT_WORKBENCH_CRAFT.value(), EQUIPMENT_CRAFT, 1, 9, 10, 36);
    }
}
