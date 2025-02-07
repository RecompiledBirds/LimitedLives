package recompiled.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ScoreBoardUtils {



    private static Scoreboard board;
    public static Scoreboard GetOrSetScoreBoard(Player p){
        return p.level().getScoreboard();
    }
    public static List<ServerPlayer> AllPlayersOnServer(MinecraftServer server){
        return server.getPlayerList().getPlayers();
    }
}
