package net.dries007.tfc.common.container;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

@ParametersAreNonnullByDefault
public class ItemStackContainer extends Container
{
    protected final ItemStack stack;
    protected final PlayerEntity player;
    protected int itemIndex;
    protected int itemDragIndex;
    protected boolean isOffhand;

    public ItemStackContainer(ContainerType<?> type, int windowId, PlayerInventory playerInv, ItemStack stack)
    {
        super(type, windowId);

        this.player = playerInv.player;
        this.stack = stack;
        this.itemDragIndex = playerInv.selected;

        if (stack == player.getMainHandItem())
        {
            this.itemIndex = playerInv.selected + 27; // Mainhand opened inventory
            this.isOffhand = false;
        }
        else
        {
            this.itemIndex = -100; // Offhand, so ignore this rule
            this.isOffhand = true;
        }

        addContainerSlots();
        addPlayerInventorySlots(playerInv);
    }


    @Override
    @Nonnull
    public ItemStack quickMoveStack(PlayerEntity player, int index)
    {
        // Slot that was clicked
        Slot slot = slots.get(index);

        ItemStack itemstack;

        if (slot == null || !slot.hasItem())
            return ItemStack.EMPTY;

        if (index == itemIndex)
            return ItemStack.EMPTY;

        ItemStack itemstack1 = slot.getItem();
        itemstack = itemstack1.copy();

        // Begin custom transfer code here
        int containerSlots = slots.size() - player.inventory.getContainerSize(); // number of slots in the container
        if (index < containerSlots)
        {
            // Transfer out of the container
            if (!this.moveItemStackTo(itemstack1, containerSlots, slots.size(), true))
            {
                // Don't transfer anything
                return ItemStack.EMPTY;
            }
        }
        // Transfer into the container
        else
        {
            if (!this.moveItemStackTo(itemstack1, 0, containerSlots, false))
            {
                return ItemStack.EMPTY;
            }
        }

        if (itemstack1.getCount() == 0)
        {
            slot.set(ItemStack.EMPTY);
        }
        else
        {
            slot.setChanged();
        }
        if (itemstack1.getCount() == itemstack.getCount())
        {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, itemstack1);
        return itemstack;
    }

    @Override
    @Nonnull
    public ItemStack clicked(int slotID, int dragType, ClickType clickType, PlayerEntity player)
    {
        // Prevent moving of the item stack that is currently open
        if (slotID == itemIndex && (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP || clickType == ClickType.THROW || clickType == ClickType.SWAP))
        {
            return ItemStack.EMPTY;
        }
        else if ((dragType == itemDragIndex) && clickType == ClickType.SWAP)
        {
            return ItemStack.EMPTY;
        }
        else
        {
            return super.clicked(slotID, dragType, clickType, player);
        }

    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    protected void addContainerSlots(){};

    protected void addPlayerInventorySlots(PlayerInventory playerInv)
    {
        // Add Player Inventory Slots
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; k++)
        {
            addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }
}
