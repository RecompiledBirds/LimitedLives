package com.onelifemod;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
public class EventHandler {
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
    public static void SetLives(ServerPlayer p, int amount){
        if(amount<0)amount=0;
        CompoundTag data = GetTag(p);
        if (!data.contains(tagName)) {
            data.putInt(tagName, Config.maxLives.get());
        }
        ServerLevel level= (ServerLevel) p.level;
        Scoreboard board = level.getScoreboard();
        String name = p.getName().getString();
        double upperbound=(double)(Config.maxLives.get()/2)+1.0;
        if(amount>=upperbound){
            board.addPlayerToTeam(name, GetTeam(board, TeamNames.Green));
        }
        if(amount<=upperbound&&amount>=(double)(Config.maxLives.get()/2)-1.0){
            board.addPlayerToTeam(name, GetTeam(board, TeamNames.Yellow));
        }
        if(amount==1){
            board.addPlayerToTeam(name, GetTeam(board, TeamNames.Red));
        }

        p.level.getScoreboard().getOrCreatePlayerScore(p.getName().getString(), p.getLevel().getScoreboard().getOrCreateObjective(objectiveName)).setScore(amount);
        data.putInt(tagName, amount);
    }
    public static int ModifyPlayerLives(ServerPlayer p, int amount){
        CompoundTag data = GetTag(p);
        if (!data.contains(tagName)) {
            data.putInt(tagName, Config.maxLives.get());
        }

        int amountLeft=data.getInt(tagName) +amount;
        data.putInt(tagName, amountLeft);
        SetLives(p,amountLeft);
        return amountLeft;
    }
    public static final String persistName = "PlayerPersisted";
    public static final String tagName = "RemainingLives";
    public static final String persistTagName = "DeathData";
    public static final String connectedBefore = "ConnectedBefore";

    public static CompoundTag GetTag(LivingEntity e) {
        CompoundTag persistData = e.getPersistentData().getCompound(persistName);
        e.getPersistentData().put("PlayerPersisted", persistData);
        CompoundTag tag = persistData.getCompound(persistTagName);
        persistData.put(persistTagName, tag);
        return tag;
    }

    public enum TeamNames {
        Green,
        Yellow,
        Red;

        @Override
        public String toString() {
            switch (this) {
                case Red -> {
                    return "OLM_RED";
                }
                case Green -> {
                    return "OLM_GREEN";
                }
                case Yellow -> {
                    return "OLM_YELLOW";
                }
            }
            return "";
        }
    }


    public static PlayerTeam GetTeam(Scoreboard scoreboard, TeamNames name) {
        return scoreboard.getPlayerTeam(name.toString());
    }

    public static final String objectiveName = "Lives";

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
    private static boolean FirstTimeConnection(Player player, CompoundTag data) {
        ServerLevel world = (ServerLevel) player.level;

        boolean sameSpawn = player.blockPosition().closerThan(world.getSharedSpawnPos(), 50);
        boolean dataShowsConnected = (data.contains(connectedBefore) && data.getBoolean(connectedBefore));
        return sameSpawn && !dataShowsConnected;
    }
}
