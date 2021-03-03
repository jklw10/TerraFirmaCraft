/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import javax.annotation.Nullable;

import net.dries007.tfc.common.container.TFCContainerProviders;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.container.IButtonHandler;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Supplier;

/**
 * This is a generic packet that sends a button notification to the players open container, which can delegate to the tile entity if needed
 * See {@link net.dries007.tfc.client.screen.button.KnappingButton} for an example of its usage, and {@link net.dries007.tfc.client.screen.KnappingScreen} for an example of the message handling
 *
 * @author AlcatrazEscapee
 */
public class ScreenButtonPacket
{
    private int buttonID;
    private CompoundNBT extraNBT;

    @SuppressWarnings("unused")
    @Deprecated
    public ScreenButtonPacket() {}

    public ScreenButtonPacket(int buttonID, @Nullable CompoundNBT extraNBT)
    {
        this.buttonID = buttonID;
        this.extraNBT = extraNBT;
    }

    public ScreenButtonPacket(int buttonID)
    {
        this(buttonID, null);
    }
    ScreenButtonPacket(PacketBuffer buffer)
    {
        this.buttonID = buffer.readInt();
        this.extraNBT = buffer.readNbt();
    }

    void encode(PacketBuffer buffer)
    {
        buffer.writeInt(buttonID);
        if(extraNBT != null)
        {
            buffer.writeByteArray(extraNBT.getByteArray(""));
        }
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().setPacketHandled(true);
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null)
            {
                if (player != null)
                {
                    if (player.containerMenu instanceof IButtonHandler)
                    {
                        ((IButtonHandler)player.containerMenu).onButtonPress(buttonID, extraNBT);
                    }
                }
            }
        });
    }
}
