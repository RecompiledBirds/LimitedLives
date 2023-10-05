package com.onelifemod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WorldBorderHandler {
    //We cant have a config loaded during EntityJoinLevel, so we need to have this...
    //This is not good.
    @SubscribeEvent
    public void TickCounterHandler(TickEvent.PlayerTickEvent event){
        if(!event.side.isServer())return;
        if(!Config.worldBorderExpands.get())return;
        ServerPlayer player = (ServerPlayer)event.player;
        ServerLevel level = player.getLevel();
        //first time creation logic
        if(Config.SetDefaultBorderSize()){
            WorldBorderUtility.GenerateWorldBorder(player);
        }



        //days passed logic
        boolean dayPassed =event.player.level.getGameTime()%(23999L *Config.daysBetweenExpansion.get())==0;
        if(!dayPassed||!(Config.worldBorderExpansionMode.get()== Config.WorldBorderMode.Day||Config.worldBorderExpansionMode.get()== Config.WorldBorderMode.Both))return;
        Config.WorldBorderMode mode= Config.worldBorderExpansionMode.get();
        double size = level.getWorldBorder().getSize();
        size+= mode== Config.WorldBorderMode.Day? Config.worldBorderExpansionSize.get():Config.worldBorderExpansionSizePerDay.get();
        level.getWorldBorder().setSize(size);
    }

    @SubscribeEvent
    public void PlayerLevelUpHandler(PlayerXpEvent.LevelChange event){
        if(!Config.worldBorderExpands.get())return;
        if(!(Config.worldBorderExpansionMode.get()== Config.WorldBorderMode.XP||Config.worldBorderExpansionMode.get()== Config.WorldBorderMode.Both))return;
        ServerPlayer player = (ServerPlayer)event.getEntity();
        ServerLevel level = player.getLevel();
        double size = level.getWorldBorder().getSize();
        size+=  Config.worldBorderExpansionSize.get()*event.getLevels();
        level.getWorldBorder().setSize(size);
    }

    @SubscribeEvent
    public void SpawnHandler(EntityJoinLevelEvent event){

    }

}
