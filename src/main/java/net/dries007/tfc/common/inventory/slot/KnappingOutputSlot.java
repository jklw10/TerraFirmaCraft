package net.dries007.tfc.common.inventory.slot;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class KnappingOutputSlot extends SlotItemHandler
{
    private final Runnable onSlotTake;

    public KnappingOutputSlot(IItemHandler inventory, int idx, int x, int y, Runnable onSlotTake)
    {
        super(inventory, idx, x, y);
        this.onSlotTake = onSlotTake;
    }

    @Override
    @Nonnull
    public ItemStack onTake(PlayerEntity thePlayer, @Nonnull ItemStack stack)
    {
        onSlotTake.run();
        return super.onTake(thePlayer, stack);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }
}
