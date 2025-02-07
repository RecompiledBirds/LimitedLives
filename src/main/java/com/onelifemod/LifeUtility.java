package com.onelifemod;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.apache.logging.log4j.Level;
import recompiled.core.LogUtils;
import recompiled.core.ScoreBoardUtils;

import static com.onelifemod.LifeUtility.TeamNames.*;

public class LifeUtility {
    public static final String objectiveName = "LLM.Lives";
    public static boolean FirstTimeConnection(ServerPlayer player) {
        Scoreboard board = ScoreBoardUtils.GetOrSetScoreBoard(player);
        return !board.hasPlayerScore(GetNameForBoard(player),LifeObjective(board));
    }


    public static int ModifyPlayerLives(ServerPlayer p, int amount){
        Scoreboard board = ScoreBoardUtils.GetOrSetScoreBoard(p);
        Score score=GetLifeScore(board,p);
        String name =  GetNameForBoard(p);
        if(!board.hasPlayerScore(name,LifeObjective(board))){
            int res = Config.GetMaxLives()+amount;
            score.setScore(res);
            return res;
        }
        if(amount>0) {
            LifeSimpleChannel.SendGiveLifePacketToPlayer(p);
            ServerLevel l = p.serverLevel();

            l.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,p.position().x,p.position().y,p.position().z,200,0,0,0,0.5   );
            l.sendParticles(ParticleTypes.FIREWORK,p.position().x,p.position().y,p.position().z,200,0,0,0,0.5);
            l.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,p.position().x,p.position().y,p.position().z,200,0,0,0,0.1);
        }
        int amountLeft= score.getScore()+amount;
        return SetLives(p,amountLeft,board,score);

    }
    private static Objective LifeObjective(ServerPlayer p){
        return LifeObjective(p.level().getScoreboard());
    }
    private static Objective LifeObjective(Scoreboard board){
        return board.getOrCreateObjective(LifeUtility.objectiveName);
    }
    public static int SetLives(ServerPlayer p, int amount){
        Scoreboard board = ScoreBoardUtils.GetOrSetScoreBoard(p);
        Score score= GetLifeScore(board,p);
        return SetLives(p,amount,board,score);
    }
    public static String GetNameForBoard(ServerPlayer p){
        if(Config.livesSharedBetweenAllPlayers.get()){
            return "Shared Lives";
        }
        return p.getName().getString();
    }
    public static int SetLives(ServerPlayer p, int amount, Scoreboard board, Score lifeScore){
        if(amount<0)amount=0;
        String name = GetNameForBoard(p);
        lifeScore.setScore(amount);

        double upperbound = (double) (Config.GetMaxLives() / 2) + 1.0;
        if(!Config.showTeams.get())
            return lifeScore.getScore();
        if (amount >= upperbound) {
            board.addPlayerToTeam(name, GetTeam(board, TeamNames.Green));
        }
        if (amount <= upperbound && amount >= (double) (Config.maxLives.get() / 2) - 1.0) {
            board.addPlayerToTeam(name, GetTeam(board, TeamNames.Yellow));
        }
        if (amount == 1) {
            board.addPlayerToTeam(name, GetTeam(board, TeamNames.Red));
        }
        if(amount==0){
            if(board.getPlayerTeam(name)!=null)
                board.removePlayerFromTeam(name, GetTeam(board,TeamNames.Red));
        }
        return lifeScore.getScore();
    }
    public static Score GetLifeScore(ServerPlayer p){
        return GetLifeScore(ScoreBoardUtils.GetOrSetScoreBoard(p),p);
    }
    public static Score GetLifeScore(Scoreboard board,ServerPlayer p){
        return board.getOrCreatePlayerScore(GetNameForBoard(p),LifeObjective(board));
    }
    public static int GetLives(ServerPlayer p){
        return GetLifeScore(p).getScore();
    }

    public enum TeamNames {
        Green,
        Yellow,
        Red;

        @Override
        public String toString() {
            return switch (this) {
                case Red -> "LLM_RED";

                case Green -> "LLM_GREEN";

                case Yellow ->  "LLM_YELLOW";
            };
        }
    }


    public static PlayerTeam GetTeam(Scoreboard scoreboard, TeamNames name) {
        PlayerTeam team =  scoreboard.getPlayerTeam(name.toString());
        if (team!=null)return team;
        switch (name){
            case Yellow:
                team=scoreboard.addPlayerTeam(Yellow.toString());
                team.setColor(ChatFormatting.YELLOW);
                break;
            case Green:
                team=scoreboard.addPlayerTeam(Green.toString());
                team.setColor(ChatFormatting.GREEN);
                break;
            case Red:
                team=scoreboard.addPlayerTeam(Red.toString());
                team.setColor(ChatFormatting.RED);
                break;
        }
        return team;
    }

}
