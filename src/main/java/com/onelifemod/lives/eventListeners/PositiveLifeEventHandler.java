package com.onelifemod.lives.eventListeners;

import com.onelifemod.common.GameRuleHelper;
import com.onelifemod.lives.LifeUtility;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import recompiled.core.ScoreBoardUtils;

import java.util.function.Function;

public class PositiveLifeEventHandler {
    @SubscribeEvent()
    public void HandleAnimalTamed(AnimalTameEvent event) {
        ServerPlayer player = (ServerPlayer) event.getTamer();

        if (!GameRuleHelper.AllowGainingLivesThroughTamingAnimals(player.serverLevel())) {
            return;
        }
        GetScore("LLM.TamedAnimals",player,(input)->input+1);
    }

    private void GetScore(String objectiveName, ServerPlayer player, Function<Integer,Integer> changeValueFunction) {
        Scoreboard board = ScoreBoardUtils.GetOrSetScoreBoard(player);
        String playerName = LifeUtility.GetNameForBoard(player);
        Objective currentObj = board.getOrCreateObjective(objectiveName);

        Score currentScore = board.getOrCreatePlayerScore(playerName, currentObj);

        Objective requiredScoreObjective = board.getOrCreateObjective("required" + objectiveName);

        Score requiredScore = board.getOrCreatePlayerScore(playerName + "_required" + objectiveName, requiredScoreObjective);
        int score = currentScore.getScore() + 1;
        int required = requiredScore.getScore();
        if (required == 0) required = 1;
        if (score < required) {
            currentScore.setScore(score + 1);
            return;
        }
        requiredScore.setScore(changeValueFunction.apply(required));
        currentScore.setScore(0);
        LifeUtility.ModifyPlayerLives(player, 1,objectiveName);
    }

    @SubscribeEvent
    public void HandleAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!GameRuleHelper.AdvancementsGiveLives(player.serverLevel()) || event.getAdvancement().getParent() == null || event.getAdvancement().getRewards().getRecipes().length > 0)
            return;

        GetScore("LLM.AdvancementCountObjective",player,(input)->input+3);


    }
}
