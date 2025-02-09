package recompiled.core;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.List;

public class ScoreBoardUtils {



    private static Scoreboard board;
    public static Scoreboard GetOrSetScoreBoard(Player p){
        return p.level().getScoreboard();
    }
    public static List<ServerPlayer> AllPlayersOnServer(MinecraftServer server){
        return server.getPlayerList().getPlayers();
    }

    /*

    This seems weird but fixes some bugs.
     */
    public static Objective GetOrRessuringlyCreateObjective(Scoreboard board, String objectiveName){
        if(!board.hasObjective(objectiveName)){
            return board.addObjective(objectiveName, ObjectiveCriteria.DUMMY, Component.literal(objectiveName), ObjectiveCriteria.RenderType.INTEGER);
        }
        return board.getOrCreateObjective(objectiveName);
    }
}
