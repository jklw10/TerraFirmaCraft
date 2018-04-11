package net.dries007.tfc.objects.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public final class ItemsTFC
{
    private ItemsTFC() {}

    public static final ItemDebug WAND = null;

    private static ImmutableList<Item> allSimpleItems;
    private static ImmutableList<ItemOreTFC> allOreItems;

    public static ImmutableList<Item> getAllSimpleItems()
    {
        return allSimpleItems;
    }
    public static ImmutableList<ItemOreTFC> getAllOreItems()
    {
        return allOreItems;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> r = event.getRegistry();

        Builder<Item> simpleItems = ImmutableList.builder();

        simpleItems.add(register(r, "wand", new ItemDebug(), CT_MISC));

        BlocksTFC.getAllNormalItemBlocks().forEach(x -> register_item_block(r, x));
        BlocksTFC.getAllInventoryItemBlocks().forEach(x -> register_item_block(r, x));

        {
            for (Rock rock : Rock.values())
                simpleItems.add(register(r, ("rock_" + rock.name()).toLowerCase(), new ItemRock(rock), CT_ROCK_ITEMS));
            for (Rock rock : Rock.values())
                simpleItems.add(register(r, ("brick_" + rock.name()).toLowerCase(), new ItemBrickTFC(rock), CT_ROCK_ITEMS));
        }

        {
            Builder<ItemOreTFC> b = new Builder<>();
            for (Ore ore : Ore.values())
                b.add(register(r, ("ore_" + ore.name()).toLowerCase(), new ItemOreTFC(ore), CT_ORE_ITEMS));
            allOreItems = b.build();
        }

        allSimpleItems = simpleItems.build();
    }

    private static void register_item_block(IForgeRegistry<Item> r, Block block)
    {
        r.register(new ItemBlock(block).setRegistryName(block.getRegistryName()).setCreativeTab(block.getCreativeTabToDisplayOn()));
    }

    private static <T extends Item> T register(IForgeRegistry<Item> r, String name, T item, CreativeTabs ct)
    {
        item.setRegistryName(MOD_ID, name);
        item.setUnlocalizedName(MOD_ID + "." + name.replace('_', '.'));
        item.setCreativeTab(ct);
        r.register(item);
        return item;
    }
}