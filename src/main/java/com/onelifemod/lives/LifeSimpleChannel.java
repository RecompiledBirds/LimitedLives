package com.onelifemod.lives;

import com.onelifemod.limitedLives;
import com.onelifemod.lives.GiveLifePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class LifeSimpleChannel {
    private static  final String Protocol="0.0.1";
    public static SimpleChannel Instance ;
    public static void register(){
        Instance =NetworkRegistry.newSimpleChannel(
                new ResourceLocation(limitedLives.MOD_ID,"main"),
                ()->Protocol,
                Protocol::equals,
                Protocol::equals);
        Instance.messageBuilder(GiveLifePacket.class,0,NetworkDirection.PLAY_TO_CLIENT)
                .consumerMainThread(GiveLifePacket::handle)
                .encoder(GiveLifePacket::encode)
                .decoder(GiveLifePacket::new)
                .add();
    }


    public static void SendGiveLifePacketToPlayer(ServerPlayer player){
       Instance.send(PacketDistributor.PLAYER.with(() -> player), new GiveLifePacket());
    }

}
