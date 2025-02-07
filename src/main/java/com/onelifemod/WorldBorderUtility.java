package com.onelifemod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import recompiled.core.ScoreBoardUtils;


public class WorldBorderUtility {
    public static final String objectiveName = "WorldGenerated";
    public static void GenerateWorldBorder(ServerPlayer player, ServerLevel level) {


        Scoreboard levelBoard = ScoreBoardUtils.GetOrSetScoreBoard(player);
        if (!levelBoard.getObjectiveNames().contains(objectiveName)) {
            WorldBorder border = level.getWorldBorder();
            border.setCenter(player.position().x, player.position().z);
            border.setSize(Config.startingWorldBorderSize.get());
            levelBoard.addObjective(objectiveName, ObjectiveCriteria.DUMMY, Component.literal(objectiveName), ObjectiveCriteria.RenderType.INTEGER);
        }
    }
}
