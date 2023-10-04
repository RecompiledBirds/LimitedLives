package com.onelifemod;

import com.onelifemod.Utility.TeamNames;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.onelifemod.Utility.*;

public class LifeEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void HandleRespawn(PlayerEvent.PlayerRespawnEvent event) {
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



    @SubscribeEvent
    public void HandleJoinedPlayer(TickEvent.PlayerTickEvent event) {
        if (!event.side.isServer() || !(event.player instanceof ServerPlayer)) return;
        ServerPlayer player = (ServerPlayer) event.player;
        CompoundTag data = GetTag(player);
        if (data.contains(connectedBefore) || data.getBoolean(connectedBefore)) return;

        ServerLevel level = player.getLevel();

        String name = player.getName().getString();
        Scoreboard levelBoard = level.getScoreboard();
        if (FirstTimeConnection(player, data)) {
            data.putBoolean(connectedBefore, true);
            if (levelBoard.getPlayerTeams().isEmpty()) {
                PlayerTeam green = level.getScoreboard().addPlayerTeam(TeamNames.Green.toString());
                green.setColor(ChatFormatting.GREEN);
                PlayerTeam yellow = level.getScoreboard().addPlayerTeam(TeamNames.Yellow.toString());
                yellow.setColor(ChatFormatting.YELLOW);
                PlayerTeam red = level.getScoreboard().addPlayerTeam(TeamNames.Red.toString());
                red.setColor(ChatFormatting.RED);

            }
            if (!levelBoard.getObjectiveNames().contains(objectiveName))
                level.getScoreboard().addObjective(objectiveName, ObjectiveCriteria.DUMMY, Component.literal(objectiveName), ObjectiveCriteria.RenderType.INTEGER);
            levelBoard.getOrCreatePlayerScore(name, levelBoard.getOrCreateObjective(objectiveName)).setScore(Config.maxLives.get());
            levelBoard.setDisplayObjective(1, levelBoard.getObjective(objectiveName));
            levelBoard.addPlayerToTeam(name, GetTeam(levelBoard, TeamNames.Green));
        }

    }
    @SubscribeEvent
    public void RegisterCMDS(RegisterCommandsEvent event){
        GiveLife.register(event.getDispatcher());
    }
}