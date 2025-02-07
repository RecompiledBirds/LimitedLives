package com.onelifemod;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;
import recompiled.core.LogUtils;


import java.util.function.Supplier;

public class GiveLifePacket {
   public GiveLifePacket(){

   }
    public GiveLifePacket(FriendlyByteBuf friendlyByteBuf) {

    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {

       LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"GotLife packet received..."+contextSupplier.get().getDirection().toString());
       if(!Config.showNetherStarOnGainedLife.get())return;
       Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(Items.NETHER_STAR));

    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
    }
}
