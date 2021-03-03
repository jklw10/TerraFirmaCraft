package net.dries007.tfc.common.recipes.knapping;

import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.SimpleCraftMatrix;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class KnappingRecipe implements ISimpleRecipe<RecipeWrapper>
{
    public static final IndirectHashCollection<Item, KnappingRecipe> CACHE = new IndirectHashCollection<>(recipe -> recipe.getIngredient().getValidItems());

    private final KnappingType type;
    private final SimpleCraftMatrix matrix;
    private final KnappingIngredient ingredient;

    protected KnappingRecipe(KnappingType type, boolean outsideSlotRequired, String... pattern)
    {
        this.matrix = new SimpleCraftMatrix(outsideSlotRequired, pattern);
        this.type = type;
    }

    public KnappingIngredient getIngredient()
    {
        return ingredient;
    }

    @Override
    public boolean matches(RecipeWrapper inv, World worldIn) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }
    public SimpleCraftMatrix getMatrix()
    {
        return matrix;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.KNAPPING.get();
    }

    @Override
    public IRecipeType<?> getType()
    {
        return TFCRecipeTypes.KNAPPING;
    }
    public static class Serializer<R extends KnappingRecipe> extends RecipeSerializer<R>
    {
        private final KnappingRecipe.Serializer.Factory<R> factory;

        public Serializer(KnappingRecipe.Serializer.Factory<R> factory)
        {
            this.factory = factory;
        }

        @Override
        public R fromJson(ResourceLocation recipeId, JsonObject json)
        {
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(json.get("ingredient"));
            boolean copyInputState = JSONUtils.getAsBoolean(json, "copy_input", false);
            BlockState state;
            if (!copyInputState)
            {
                state = Helpers.readBlockState(JSONUtils.getAsString(json, "result"));
            }
            else
            {
                state = Blocks.AIR.defaultBlockState();
            }
            return factory.create(recipeId, ingredient, state, copyInputState);
        }

        @Nullable
        @Override
        public R fromNetwork(ResourceLocation recipeId, PacketBuffer buffer)
        {
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(buffer);
            boolean copyInputState = buffer.readBoolean();
            BlockState state;
            if (!copyInputState)
            {
                state = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS).defaultBlockState();
            }
            else
            {
                state = Blocks.AIR.defaultBlockState();
            }
            return factory.create(recipeId, ingredient, state, copyInputState);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, R recipe)
        {
            IBlockIngredient.Serializer.INSTANCE.write(buffer, recipe.ingredient);
            buffer.writeBoolean(recipe.copyInputState);
            if (!recipe.copyInputState)
            {
                buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, recipe.outputState.getBlock());
            }
        }

        public interface Factory<R extends KnappingRecipe>
        {
            R create(ResourceLocation id, IBlockIngredient ingredient, BlockState state, boolean copyInputState);
        }
    }
}
