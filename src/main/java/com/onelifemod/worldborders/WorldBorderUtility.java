package com.onelifemod.worldborders;

import com.onelifemod.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.logging.log4j.Level;
import recompiled.core.LogUtils;
import recompiled.core.ScoreBoardUtils;


public class WorldBorderUtility {
    public static final String objectiveName = "LLM.WorldGenerated";
    public static void GenerateWorldBorder(ServerPlayer player, ServerLevel level) {
        Scoreboard levelBoard = level.getScoreboard();
        if (!levelBoard.getObjectiveNames().contains(objectiveName)) {
            WorldBorder border = level.getWorldBorder();
            border.setCenter(player.position().x, player.position().z);
            border.setSize(Config.startingWorldBorderSize.get());
            levelBoard.addObjective(objectiveName, ObjectiveCriteria.DUMMY, Component.literal(objectiveName), ObjectiveCriteria.RenderType.INTEGER);
        }
    }
}
