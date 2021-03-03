package net.dries007.tfc.common.container;

import javax.annotation.Nullable;

import net.dries007.tfc.common.inventory.slot.KnappingOutputSlot;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.mixin.item.crafting.RecipeManagerAccessor;
import net.dries007.tfc.mixin.item.crafting.RecipeManagerMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.common.recipes.knapping.KnappingType;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.SimpleCraftMatrix;


public class KnappingContainer extends ItemStackContainer implements IButtonHandler
{
    private final SimpleCraftMatrix matrix;
    private final KnappingType type;
    private final ItemStack stackCopy;
    public boolean requiresReset;
    private boolean hasBeenModified;

    public KnappingContainer(ContainerType<SimpleContainer> cType, int windowId, KnappingType type, PlayerInventory playerInv, ItemStack stack)
    {
        super(cType,windowId,playerInv, stack);
        this.itemIndex += 1;
        this.type = type;
        this.stackCopy = this.stack.copy();

        matrix = new SimpleCraftMatrix();
        hasBeenModified = false;
        requiresReset = false;
    }

    @Override
    public void onButtonPress(int buttonID, @Nullable CompoundNBT extraNBT)
    {
        matrix.set(buttonID, false);

        if (!hasBeenModified)
        {
            if (!player.isCreative() && !type.consumeAfterComplete())
            {
                consumeItem();
            }
            hasBeenModified = true;
        }

        // check the pattern
        Slot slot = slots.get(0);
        if (slot != null)
        {
            KnappingRecipe recipe = getMatchingRecipe();
            if (recipe != null)
            {
                slot.set(recipe.getResultItem());
            }
            else
            {
                slot.set(ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void removed(PlayerEntity playerIn) {
        Slot slot = slots.get(0);
        ItemStack stack = slot.getItem();
        if (!stack.isEmpty())
        {
            if (player.getCommandSenderWorld().isClientSide)
            {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                consumeIngredientStackAfterComplete();
            }
        }
        super.removed(playerIn);
    }

    /**
     * Used in client to check a slot state in the matrix
     * JEI won't cause issues anymore see https://github.com/TerraFirmaCraft/TerraFirmaCraft/issues/718
     *
     * @param index the slot index
     * @return the boolean state for the checked slot
     */
    public boolean getSlotState(int index)
    {
        return matrix.get(index);
    }

    /**
     * Used in client to set a slot state in the matrix
     * JEI won't cause issues anymore see https://github.com/TerraFirmaCraft/TerraFirmaCraft/issues/718
     *
     * @param index the slot index
     * @param value the value you wish to set the state to
     */
    public void setSlotState(int index, boolean value)
    {
        matrix.set(index, value);
    }

    @Override
    protected void addContainerSlots()
    {
        addSlot(new KnappingOutputSlot(new ItemStackHandler(1), 0, 128, 44, this::resetMatrix));
    }

    @Override
    protected void addPlayerInventorySlots(PlayerInventory playerInv)
    {
        // Add Player Inventory Slots (lower down)
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 18));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlot(new Slot(playerInv, k, 8 + k * 18, 142 + 18));
        }
    }

    private void resetMatrix()
    {
        matrix.setAll(false);
        requiresReset = true;
        consumeIngredientStackAfterComplete();
    }

    private KnappingRecipe getMatchingRecipe()
    {
        return KnappingRecipe.CACHE.values().stream().filter(x -> x.getMatrix().matches(matrix)).findFirst().get();
    }
    public void consumeItem()
    {
        ItemStack stack = this.stack;
        stack.setCount(this.stack.getCount()-type.getAmountToConsume());
        if (isOffhand)
        {
            player.inventory.offhand.set(0,stack);
        }
        else
        {
            player.inventory.setItem(player.inventory.selected, stack);
        }
    }
    private void consumeIngredientStackAfterComplete()
    {
        if (type.consumeAfterComplete())
        {
            consumeItem();
        }
    }
}
