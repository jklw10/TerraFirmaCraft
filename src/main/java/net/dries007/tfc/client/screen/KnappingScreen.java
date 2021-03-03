package net.dries007.tfc.client.screen;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.common.container.SimpleContainer;
import net.dries007.tfc.common.recipes.knapping.KnappingType;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.dries007.tfc.client.screen.button.KnappingButton;
import net.minecraft.util.text.StringTextComponent;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;


public class KnappingScreen extends TFCContainerScreen<KnappingContainer>
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/knapping.png");
    private final ResourceLocation buttonBackground;
    private final ResourceLocation buttonTexture;
    private final KnappingType type;

    public KnappingScreen(KnappingContainer container, PlayerInventory playerInventory, KnappingType type, ResourceLocation buttonTexture, ResourceLocation buttonBackground) {
        super(container, playerInventory, new StringTextComponent(type.getKnappingScreenName()), BACKGROUND);
        this.buttonBackground = buttonBackground;
        this.buttonTexture = buttonTexture;
        this.type = type;
    }

    @Override
    public void init(){
        super.init();
        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                int bx = (width - getXSize()) / 2 + 12 + 16 * x;
                int by = (height - getYSize()) / 2 + 12 + 16 * y;
                addButton(new KnappingButton(x + 5 * y, bx, by, 16, 16, buttonTexture,x*16,y*16, this::onClick));
            }
        }
        // JEI reloads this after it's recipe gui is closed
        /* TODO: jei support for knapping is this needed?
        if ( instanceof KnappingContainer)
        {
            .requiresReset = true;
        }//*/
    }

    @Override
    public void renderBackground(MatrixStack matrixStack) {
        super.renderBackground(matrixStack);
        for(Widget button : buttons)
        {
            if(!button.visible && button instanceof KnappingButton)
            {
                blit(matrixStack, button.x, button.y, 0, (float) button.x*16, (float) button.y*16, 16, 16, 5*height, 5*width);
            }
        }
    }

    public void onClick(KnappingButton button)
    {
       //button.playPressSound(mc.getSoundHandler()); button side.
       // Set the client-side matrix
       if (quickCraftSlots instanceof KnappingContainer)
       {
           ((KnappingContainer) quickCraftSlots).setSlotState(button.getId(), false);
       }
    }


}
