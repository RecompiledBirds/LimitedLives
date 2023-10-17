package com.onelifemod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ISystemReportExtender;

import static com.onelifemod.LifeUtility.objectiveName;

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

        String objName = "borderTracker";

        Scoreboard board = level.getScoreboard();
        String name = player.getName().getString();
        if (!board.getObjectiveNames().contains(objName)) {
            board.addObjective(objName, ObjectiveCriteria.DUMMY, Component.literal(objName), ObjectiveCriteria.RenderType.INTEGER);
            board.getOrCreatePlayerScore(name, board.getOrCreateObjective(objName)).setScore(Config.maxLives.get());
        }
        Objective objective = board.getOrCreateObjective(objName);
        board.getOrCreatePlayerScore(name,objective).setScore(board.getOrCreatePlayerScore(name,objective).getScore()+1);
        //days passed logic

        int score = board.getOrCreatePlayerScore(name, objective).getScore();
        int timeNeeded=23000 *Config.daysBetweenExpansion.get();
        boolean dayPassed =(score>timeNeeded);
        Config.WorldBorderMode mode= Config.worldBorderExpansionMode.get();
        boolean modeCheck=(mode== Config.WorldBorderMode.Day||mode== Config.WorldBorderMode.Both);
        System.out.println(dayPassed&&modeCheck);
        if(!(dayPassed&&modeCheck))return;
        double size = level.getWorldBorder().getSize();
        size+= mode== Config.WorldBorderMode.Day? Config.worldBorderExpansionSize.get():Config.worldBorderExpansionSizePerDay.get();
        Integer min=Config.minBorderSize.get();
        Integer max=Config.maxBorderSize.get();
        if((min!=-1&&size<min) ||(max!=-1&&size>max))return;
        level.getWorldBorder().setSize(size);
        board.getOrCreatePlayerScore(name, objective).setScore(0);
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
