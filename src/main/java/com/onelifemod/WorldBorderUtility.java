package com.onelifemod;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;


public class WorldBorderUtility {
    public static final String objectiveName = "WorldGenerated";
    public static void GenerateWorldBorder(ServerPlayer player) {

        ServerLevel level = player.getLevel();
        Scoreboard levelBoard = level.getScoreboard();
        if (!levelBoard.getObjectiveNames().contains(objectiveName)) {
            player.getLevel().getWorldBorder().setCenter(player.position().x, player.position().z);
            player.getLevel().getWorldBorder().setSize(Config.startingWorldBorderSize.get());
            levelBoard.addObjective(objectiveName, ObjectiveCriteria.DUMMY, Component.literal(objectiveName), ObjectiveCriteria.RenderType.INTEGER);
        }
    }
}
