package com.onelifemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class LifeUtility {
    public static final String connectedBefore = "ConnectedBefore";
    public static final String objectiveName = "Lives";
    public static boolean FirstTimeConnection(Player player, CompoundTag data) {
        ServerLevel world = (ServerLevel) player.level;

        boolean sameSpawn = player.blockPosition().closerThan(world.getSharedSpawnPos(), 50);
        boolean dataShowsConnected = (data.contains(connectedBefore) && data.getBoolean(connectedBefore));
        return sameSpawn && !dataShowsConnected;
    }
    public static final String tagName = "RemainingLives";


    public static int ModifyPlayerLives(ServerPlayer p, int amount){
        CompoundTag data = CoreUtils.GetPersistentTag(p);
        if (!data.contains(tagName)) {
            data.putInt(tagName, Config.maxLives.get());
        }

        int amountLeft=data.getInt(tagName) +amount;
        data.putInt(tagName, amountLeft);
        SetLives(p,amountLeft);
        return amountLeft;
    }

    public static void SetLives(ServerPlayer p, int amount){
        if(amount<0)amount=0;
        CompoundTag data = CoreUtils.GetPersistentTag(p);
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
        board.getOrCreatePlayerScore(name, board.getOrCreateObjective(LifeUtility.objectiveName)).setScore(amount);
        data.putInt(tagName, amount);
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

}
