/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.screen.button;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.dries007.tfc.common.container.IButtonHandler;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.widget.button.Button;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.ScreenButtonPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.awt.*;
import java.util.function.Consumer;


//client side?
public class KnappingButton extends Button
{
    private final ResourceLocation texture;
    private final int iconU;
    private final int iconV;
    private int iconX;
    private int iconY;
    private int id;

    private Consumer<KnappingButton> buttonCallBack;
    public KnappingButton(int id, int x, int y, int width, int height, ResourceLocation texture, int iconU, int iconV, Consumer<KnappingButton> buttonCallback)
    {
        super(x,y,width,height, new StringTextComponent(""), Button::onPress);
        this.texture = texture;
        iconX=x;
        iconY=y;
        this.iconU=iconU;
        this.iconV=iconV;
        this.id=id;
        this.buttonCallBack=buttonCallback;
    }

    public int getId(){
        return id;
    }

    @Override
    public void onPress()
    {
        if (this.active)
        {
            this.visible = false;

            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ScreenButtonPacket(id));
            buttonCallBack.accept(this);
            playDownSound(Minecraft.getInstance().getSoundManager());
        }
    }
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(texture);
        RenderSystem.disableDepthTest();
        blit(matrixStack, x, y, 0, (float) iconU, (float) iconV, width, height, 5*height, 5*width);
        RenderSystem.enableDepthTest();
    }
}
