package net.dries007.tfc.common.recipes.knapping;

import net.dries007.tfc.util.SimpleCraftMatrix;

public class KnappingIngredient
{
    private final KnappingType type;
    private final SimpleCraftMatrix matrix;

    public KnappingIngredient(KnappingType type, SimpleCraftMatrix matrix) {
        this.type = type;
        this.matrix = matrix;
    }
}
