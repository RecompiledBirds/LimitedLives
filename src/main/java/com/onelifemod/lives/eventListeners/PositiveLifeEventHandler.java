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

public class PositiveLifeEventHandler {
    @SubscribeEvent()
    public void HandleAnimalTamed(AnimalTameEvent event) {
        ServerPlayer player = (ServerPlayer) event.getTamer();

        if (!GameRuleHelper.AllowGainingLivesThroughTamingAnimals(player.serverLevel())) {
            return;
        }
        Scoreboard board = ScoreBoardUtils.GetOrSetScoreBoard(player);
        String name = LifeUtility.GetNameForBoard(player);

        Objective tamedObjective = board.getOrCreateObjective("tamedAnimals");

        Score tamedScore = board.getOrCreatePlayerScore(name, tamedObjective);

        Objective requiredTamedObjective = board.getOrCreateObjective("requiredTamedAnimals");

        Score requiredTamedScore = board.getOrCreatePlayerScore(name + "_requiredTamed", requiredTamedObjective);

        int newTamedScore = tamedScore.getScore() + 1;
        int reqScore = requiredTamedScore.getScore();

        if (reqScore == 0) reqScore = 1;
        if (newTamedScore < reqScore) {
            tamedScore.setScore(newTamedScore);
            return;
        }
        requiredTamedScore.setScore((reqScore) + 1);
        tamedScore.setScore(0);
        LifeUtility.ModifyPlayerLives(player, 1,"TamedAnimal");
    }

    @SubscribeEvent
    public void HandleAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!GameRuleHelper.AdvancementsGiveLives(player.serverLevel()) || event.getAdvancement().getParent() == null || event.getAdvancement().getRewards().getRecipes().length > 0)
            return;


        Scoreboard board = ScoreBoardUtils.GetOrSetScoreBoard(player);
        String name = LifeUtility.GetNameForBoard(player);

        Objective advancementObjective = board.getOrCreateObjective("LLM.advancementCountObjective");

        Score advancementScore = board.getOrCreatePlayerScore(name, advancementObjective);

        Objective requiredAdvancementObjective = board.getOrCreateObjective("LLM.requiredAdvancementCountObjective");

        Score requiredAdvancementScore = board.getOrCreatePlayerScore(name + "_AdvancementsRequiredLLM", requiredAdvancementObjective);

        int score = advancementScore.getScore() + 1;
        int required = requiredAdvancementScore.getScore();
        if (required == 0) required = 1;
        if (score < required) {
            advancementScore.setScore(score + 1);
            return;
        }
        requiredAdvancementScore.setScore(required + 3);
        advancementScore.setScore(0);
        LifeUtility.ModifyPlayerLives(player, 1,"HandleAdvancement");
    }
}
