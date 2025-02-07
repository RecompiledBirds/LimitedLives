package com.onelifemod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import recompiled.core.ScoreBoardUtils;

public class WorldBorderHandler {
    static final int DAYLENGTH=23000;
    //We cant have a config loaded during EntityJoinLevel, so we need to have this...
    //This is not good.
    @SubscribeEvent
    public void TickCounterHandler(TickEvent.PlayerTickEvent event){
        if(!event.side.isServer())return;
        Config.WorldBorderMode mode = Config.worldBorderExpansionMode.get();
        boolean modeCheck = (mode == Config.WorldBorderMode.Day || mode == Config.WorldBorderMode.Both);
        if(!modeCheck)return;
        if(!Config.worldBorderExpands.get())return;
        ServerPlayer player = (ServerPlayer)event.player;
        try (ServerLevel level = player.serverLevel()) {
            //first time creation logic
            if (Config.SetDefaultBorderSize()) {
                WorldBorderUtility.GenerateWorldBorder(player,level);
            }

            String objName = "borderTracker";

            Scoreboard board = ScoreBoardUtils.GetOrSetScoreBoard(player);
            String name = player.getName().getString();
            if (!board.getObjectiveNames().contains(objName)) {
                board.addObjective(objName, ObjectiveCriteria.DUMMY, Component.literal(objName), ObjectiveCriteria.RenderType.INTEGER);
                board.getOrCreatePlayerScore(name, board.getOrCreateObjective(objName)).setScore(Config.GetMaxLives());
            }
            Objective objective = board.getOrCreateObjective(objName);
            board.getOrCreatePlayerScore(name, objective).setScore(board.getOrCreatePlayerScore(name, objective).getScore() + 1);
            //days passed logic

            int score = board.getOrCreatePlayerScore(name, objective).getScore();
            int timeNeeded = DAYLENGTH * Config.daysBetweenExpansion.get();
            boolean dayPassed = (score > timeNeeded);



            if (!(dayPassed)) return;
            double size = level.getWorldBorder().getSize();
            size +=  Config.worldBorderExpansionSizePerDay.get();
            Integer min = Config.minBorderSize.get();
            Integer max = Config.maxBorderSize.get();
            if ((min != -1 && size < min) || (max != -1 && size > max)) return;
            level.getWorldBorder().setSize(size);
            board.getOrCreatePlayerScore(name, objective).setScore(0);
        } catch (Exception ignored) {

        }

    }

    @SubscribeEvent
    public void PlayerLevelUpHandler(PlayerXpEvent.LevelChange event){
        if(!Config.worldBorderExpands.get())return;
        if(!(Config.worldBorderExpansionMode.get()== Config.WorldBorderMode.XP||Config.worldBorderExpansionMode.get()== Config.WorldBorderMode.Both))return;
        ServerPlayer player = (ServerPlayer)event.getEntity();
        try (Level level = player.level()) {
            double size = level.getWorldBorder().getSize();
            size += Config.worldBorderExpansionSizePerXPLevel.get() * event.getLevels();
            level.getWorldBorder().setSize(size);
        } catch (Exception ignored) {

        }
    }

    @SubscribeEvent
    public void SpawnHandler(EntityJoinLevelEvent event){

    }

}
