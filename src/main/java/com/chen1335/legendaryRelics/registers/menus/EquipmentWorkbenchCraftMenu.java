package com.chen1335.legendaryRelics.registers.menus;

import com.chen1335.legendaryRelics.API.objects.LRBlocks;
import com.chen1335.legendaryRelics.API.objects.LRMenus;
import com.chen1335.legendaryRelics.API.objects.LRRecipe;
import com.chen1335.legendaryRelics.events.PlayerCraftEvent;
import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraft;
import com.chen1335.legendaryRelics.registers.recipes.EquipmentWorkbenchCraftInput;
import com.chen1335.legendaryRelics.registers.recipes.ingredients.IngredientWithSize;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class EquipmentWorkbenchCraftMenu extends AbstractContainerMenu {
    private final Inventory playerInventory;
    private final ContainerLevelAccess access;
    private final Player player;

    public final SimpleContainer craftSlots = new SimpleContainer(9);

    private final ResultSlot resultSlot;

    public EquipmentWorkbenchCraftMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public EquipmentWorkbenchCraftMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(LRMenus.EQUIPMENT_WORKBENCH_CRAFT.value(), containerId);
        this.playerInventory = playerInventory;
        this.player = playerInventory.player;
        this.access = access;

        int x = 60;
        int y = 52;
        ResultSlot resultSlot = new ResultSlot(access, new SimpleContainer(1), craftSlots, 0, x + 80, y + 4);
        this.resultSlot = resultSlot;
        this.addSlot(resultSlot);
        this.addSlot(new Slot(craftSlots, 0, x, y + 4) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });


        float r = (float) (Math.PI * 2 / 8);
        for (int i = 1; i <= 8; i++) {
            float r1 = r * i + Mth.PI + Mth.HALF_PI / 2;
            int l = 40;
            int x1 = (int) (Mth.cos(r1) * l) + x;
            int y1 = (int) (Mth.sin(r1) * l) + y + 4;
            this.addSlot(new Slot(craftSlots, i, x1, y1));
        }


        for (int k = 0; k < 3; k++) {
            for (int i1 = 0; i1 < 9; i1++) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18 + y));
            }
        }

        for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142 + y));
        }


        craftSlots.addListener(container -> access.execute((level, blockPos) -> this.checkRecipeAndSetResult(container, level, blockPos)));
    }

    private void checkRecipeAndSetResult(Container container, Level level, BlockPos blockPos) {
        if (container instanceof SimpleContainer simpleContainer) {
            NonNullList<ItemStack> items = simpleContainer.getItems();
            NonNullList<ItemStack> secondary = NonNullList.create();
            for (int i = 1; i < items.size(); i++) {
                if (!items.get(i).isEmpty()) {
                    secondary.add(items.get(i));
                }
            }

            EquipmentWorkbenchCraftInput input = new EquipmentWorkbenchCraftInput(items.getFirst(), secondary);
            Optional<RecipeHolder<EquipmentWorkbenchCraft>> recipeFor = level.getRecipeManager().getRecipeFor(LRRecipe.EQUIPMENT_CRAFT.value(), input, level);
            if (recipeFor.isPresent()) {
                EquipmentWorkbenchCraft value = recipeFor.get().value();
                ItemStack item = getSlot(1).getItem();
                ItemStack result = value.result().assemble(input, level.registryAccess());
                DataComponentPatch componentsPatch = item.getComponentsPatch();
                ((PatchedDataComponentMap) result.getComponents()).applyPatch(componentsPatch);
                PlayerCraftEvent post = NeoForge.EVENT_BUS.post(new PlayerCraftEvent(player, recipeFor.get(), result));
                getSlot(0).set(post.getResult());
                resultSlot.currentRecipe = recipeFor.get();
            } else {
                getSlot(0).set(ItemStack.EMPTY);
                resultSlot.currentRecipe = null;
            }
        }
    }


    protected static boolean stillValid(ContainerLevelAccess access, Player player, Block targetBlock) {
        return access.evaluate(
                (level, blockPos) -> level.getBlockState(blockPos).is(targetBlock) && player.canInteractWithBlock(blockPos, 4.0), true
        );

    }


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        ItemStack itemStack = ItemStack.EMPTY;
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem().copy();
            itemStack = itemStack1.copy();
            if (0 <= index && index <= 9) {
                if (!this.moveItemStackTo(itemStack1, 10, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemStack1, 1, 10, false)) {
                    return ItemStack.EMPTY;
                }
            }


            if (itemStack.getCount() != itemStack1.getCount() || itemStack1.isEmpty()) {
                slot.setByPlayer(itemStack1);
                slot.setChanged();
                if (index == 0) {
                    slot.onTake(player, itemStack);
                }
            }


        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, LRBlocks.EQUIPMENT_WORKBENCH.value());
    }


    public Player getPlayer() {
        return player;
    }

    private static class ResultSlot extends Slot {

        private final ContainerLevelAccess access1;
        private final Container craftSlots;

        public RecipeHolder<EquipmentWorkbenchCraft> currentRecipe = null;

        public ResultSlot(ContainerLevelAccess access, Container result, Container craftSlots, int slot, int x, int y) {
            super(result, slot, x, y);
            access1 = access;
            this.craftSlots = craftSlots;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            if (currentRecipe != null) {
                RecipeHolder<EquipmentWorkbenchCraft> recipeHolder = currentRecipe;
                stack.onCraftedBy(player.level(), player, stack.getCount());
                craftSlots.setItem(0, ItemStack.EMPTY);
                ArrayList<IngredientWithSize> ingredientWithSizes = new ArrayList<>(recipeHolder.value().secondaryItems());
                for (int i = 1; i < 9; i++) {
                    Iterator<IngredientWithSize> iterator = ingredientWithSizes.iterator();
                    while (iterator.hasNext()) {
                        IngredientWithSize next = iterator.next();
                        if (next.test(craftSlots.getItem(i))) {
                            craftSlots.removeItem(i, next.count());
                            iterator.remove();
                            break;
                        }
                    }
                }
                currentRecipe = null;
                access1.execute((level, blockPos) -> {
                    level.playSound(null, blockPos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1, 1);
                });
            }
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((p_39371_, p_39372_) -> this.clearContainer(player, this.craftSlots));
    }
}
