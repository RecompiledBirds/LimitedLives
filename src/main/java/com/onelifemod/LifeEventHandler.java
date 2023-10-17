package com.onelifemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.onelifemod.LifeUtility.*;
import static com.onelifemod.LifeUtility.TeamNames.*;
import static net.minecraft.ChatFormatting.*;

public class LifeEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void HandleRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if(!Config.useLivesSystem.get())return;
        if (event.isEndConquered()) return;
        ServerPlayer p = (ServerPlayer) event.getEntity();
        int livesLeft = ModifyPlayerLives(p,-1);

        if (livesLeft == 0) {
            p.displayClientMessage(Component.literal("You are out of lives..."), false);
            p.setGameMode(GameType.SPECTATOR);
            return;
        }

        p.displayClientMessage(Component.literal("You have " + livesLeft + " lives remaining..."), false);
    }


    //We cant have a config loaded during EntityJoinLevel, so we need to have this...
    //This is not good.
    @SubscribeEvent
    public void HandlePlayerTick(TickEvent.PlayerTickEvent event){
      //  if(!Config.useLivesSystem.get())return;
        if(event.side.isClient())return;
        if(!(event.player instanceof ServerPlayer player))return;

        CompoundTag data = CoreUtils.GetPersistentTag(player);
        if (data.contains(connectedBefore) || data.getBoolean(connectedBefore)) return;

        ServerLevel level = player.getLevel();

        String name = player.getName().getString();
        Scoreboard levelBoard = level.getScoreboard();
        if (FirstTimeConnection(player, data)) {
            data.putBoolean(connectedBefore, true);
            if (levelBoard.getPlayerTeams().isEmpty()) {
                PlayerTeam green = level.getScoreboard().addPlayerTeam(Green.toString());
                green.setColor(GREEN);
                PlayerTeam yellow = level.getScoreboard().addPlayerTeam(Yellow.toString());
                yellow.setColor(YELLOW);
                PlayerTeam red = level.getScoreboard().addPlayerTeam(Red.toString());
                red.setColor(RED);

            }
            if (!levelBoard.getObjectiveNames().contains(objectiveName))
               levelBoard.addObjective(objectiveName, ObjectiveCriteria.DUMMY, Component.literal(objectiveName), ObjectiveCriteria.RenderType.INTEGER);
            levelBoard.getOrCreatePlayerScore(name, levelBoard.getOrCreateObjective(objectiveName)).setScore(Config.maxLives.get());
            levelBoard.setDisplayObjective(1, levelBoard.getObjective(objectiveName));
            levelBoard.addPlayerToTeam(name, GetTeam(levelBoard, Green));
        }
    }
    @SubscribeEvent
    public void RegisterCMDS(RegisterCommandsEvent event){
        GiveLife.register(event.getDispatcher());
    }
}
