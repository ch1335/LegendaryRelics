package com.chen1335.legendaryRelics.compat.jei.recipeCategory;

import com.chen1335.legendaryRelics.API.objects.LRItems;
import com.chen1335.legendaryRelics.API.objects.LRRecipe;
import com.chen1335.legendaryRelics.LegendaryRelics;
import com.chen1335.legendaryRelics.compat.jei.LRJeiPlugin;
import com.chen1335.legendaryRelics.events.CraftResultJeiTooltipEvent;
import com.chen1335.legendaryRelics.registers.blocks.EquipmentWorkbench;
import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraft;
import com.chen1335.legendaryRelics.registers.recipes.ingredients.IngredientWithSize;
import com.mojang.serialization.Codec;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class EquipmentCraftCategory implements IRecipeCategory<RecipeHolder<EquipmentWorkbenchCraft>> {
    public static final Codec<RecipeHolder<EquipmentWorkbenchCraft>> CODEC = ResourceLocation.CODEC.xmap(resourceLocation -> {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<RecipeHolder<EquipmentWorkbenchCraft>> allRecipesFor = recipeManager.getAllRecipesFor(LRRecipe.EQUIPMENT_CRAFT.value());
        for (RecipeHolder<EquipmentWorkbenchCraft> holder : allRecipesFor) {
            if (resourceLocation.equals(holder.id())) {
                return holder;
            }
        }
        return null;
    }, RecipeHolder::id);


    public static final ResourceLocation ICON = LegendaryRelics.id("textures/gui/container/equipment_workbench_craft_jei.png");

    private final IDrawable icon;
    private final IDrawableStatic background;

    public EquipmentCraftCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(ICON, 0, 0, 175, 129);
        this.icon = guiHelper.createDrawableItemStack(LRItems.EQUIPMENT_WORKBENCH.toStack());
    }

    @Override
    public int getWidth() {
        return 175;
    }

    @Override
    public int getHeight() {
        return 129;
    }

    @Override
    public RecipeType<RecipeHolder<EquipmentWorkbenchCraft>> getRecipeType() {
        return LRJeiPlugin.EQUIPMENT_CRAFT;
    }

    @Override
    public Component getTitle() {
        return EquipmentWorkbench.CONTAINER_TITLE;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public Codec<RecipeHolder<EquipmentWorkbenchCraft>> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return CODEC;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EquipmentWorkbenchCraft> holder, IFocusGroup iFocusGroup) {
        int x = 60;
        int y = 52;

        EquipmentWorkbenchCraft craft = holder.value();

        builder.addSlot(RecipeIngredientRole.OUTPUT, x + 80, y + 4).addItemStack(craft.result()).addRichTooltipCallback((iRecipeSlotView, iTooltipBuilder) -> {
            iTooltipBuilder.add(Component.translatable("legendary_relics.keep_data"));
            NeoForge.EVENT_BUS.post(new CraftResultJeiTooltipEvent(holder, iRecipeSlotView, iTooltipBuilder));
        });

        builder.addSlot(RecipeIngredientRole.INPUT, x, y + 4).addIngredients(craft.mainItem());

        float r = (float) (Math.PI * 2 / 8);
        for (int i = 1; i <= craft.secondaryItems().size(); i++) {
            float r1 = r * i + Mth.PI + Mth.HALF_PI / 2;
            int l = 40;
            int x1 = (int) (Mth.cos(r1) * l) + x;
            int y1 = (int) (Mth.sin(r1) * l) + y + 4;
            IngredientWithSize ingredientWithSize = craft.secondaryItems().get(i - 1);
            builder.addSlot(RecipeIngredientRole.INPUT, x1, y1).addIngredients(VanillaTypes.ITEM_STACK, ingredientWithSize.getItemStacks());
        }

    }

    @Override
    public void draw(RecipeHolder<EquipmentWorkbenchCraft> holder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
    }
}
