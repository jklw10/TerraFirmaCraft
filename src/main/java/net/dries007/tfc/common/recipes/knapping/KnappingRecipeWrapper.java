package net.dries007.tfc.common.recipes.knapping;

import net.dries007.tfc.common.recipes.ItemStackRecipeWrapper;
import net.dries007.tfc.util.SimpleCraftMatrix;
import net.minecraft.item.ItemStack;

public class KnappingRecipeWrapper extends ItemStackRecipeWrapper
{
    public final SimpleCraftMatrix matrix;
    public final KnappingType type;
    public KnappingRecipeWrapper(ItemStack stack, KnappingType type, SimpleCraftMatrix matrix)
    {
        super(stack);
        this.stack = stack;
        this.matrix = matrix;
        this.type = type;
    }
}
