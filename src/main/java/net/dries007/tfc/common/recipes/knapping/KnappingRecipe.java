package net.dries007.tfc.common.recipes.knapping;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class KnappingRecipe implements ISimpleRecipe<KnappingRecipeWrapper>
{
    //TODO: implement caching and ingredient type loading.
    public static final HashMap<KnappingType, KnappingRecipe> CACHE = new HashMap<>();

    private final KnappingType type;
    private final SimpleCraftMatrix matrix;
    private final String group;
    private final ItemStack result;

    protected KnappingRecipe(KnappingType type, boolean outsideSlotRequired, String group, ItemStack result, String... pattern)
    {
        this.matrix = new SimpleCraftMatrix(outsideSlotRequired, pattern);
        this.type = type;
        this.group = group;
        this.result = result;
    }


    @Override
    public boolean matches(KnappingRecipeWrapper inv, World worldIn)
    {//todo check this works.
        return matrix.matches(inv.matrix) && type == inv.type;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
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

    //TODO:
    private static String[] patternFromJson(JsonArray jsonArr) {
        String[] astring = new String[jsonArr.size()];
        if (astring.length > SimpleCraftMatrix.MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + SimpleCraftMatrix.MAX_HEIGHT + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < astring.length; ++i) {
                String s = JSONUtils.convertToString(jsonArr.get(i), "pattern[" + i + "]");
                if (s.length() > SimpleCraftMatrix.MAX_WIDTH) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + SimpleCraftMatrix.MAX_WIDTH + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }
    //copied and modified from net/minecraft/item/crafting/ShapedRecipe.java
    public static class Serializer<KnappingRecipeWrapper> extends RecipeSerializer<KnappingRecipe>
    {
       // private static final ResourceLocation NAME = new ResourceLocation(MOD_ID, "knapping");

        @Override
        public KnappingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            //String s = JSONUtils.getAsString(json, "group", "");
            //Map<String, Ingredient> map = KnappingRecipe.keyFromJson(JSONUtils.getAsJsonObject(json, "key"));
            //
            //String[] astring = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(JSONUtils.getAsJsonArray(json, "pattern")));
            //int i = astring[0].length();
            //int j = astring.length;
            //ItemStack itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            //return new KnappingRecipe(new KnappingType(name,consumeAmmount,ConsumeAfterComplete),outSideSlot,group,result);
            return new KnappingRecipe(KnappingType.CLAY,false, "", Registry.ITEM.byId(1).getDefaultInstance(),"a ");
        }

        @Nullable
        @Override
        public KnappingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            String name = buffer.readUtf();
            int consumeAmmount = buffer.readVarInt();
            boolean ConsumeAfterComplete = buffer.readBoolean();
            boolean outSideSlot = buffer.readBoolean();
            String group = buffer.readUtf(32767);
            ItemStack result = buffer.readItem();
            return new KnappingRecipe(new KnappingType(name,consumeAmmount,ConsumeAfterComplete),outSideSlot,group,result);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, KnappingRecipe recipe)
        {
            buffer.writeUtf(recipe.type.getKnappingScreenName());
            buffer.writeInt(recipe.type.getAmountToConsume());
            buffer.writeBoolean(recipe.type.consumeAfterComplete());

            String[] sa = recipe.matrix.getPattern();
            for (String s: sa)
            {
                buffer.writeUtf(s);
            }
            buffer.writeBoolean(recipe.matrix.getOutsideSlot());

            buffer.writeUtf(recipe.group);
            buffer.writeItem(recipe.result);
        }
    }
}
