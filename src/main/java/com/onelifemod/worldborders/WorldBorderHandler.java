package com.onelifemod.worldborders;

import com.onelifemod.Config;
import com.onelifemod.common.GameRuleHelper;
import com.onelifemod.limitedLives;
import com.onelifemod.lives.eventListeners.LivesDeathHandler;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;
import recompiled.core.LogUtils;

import static com.onelifemod.lives.LifeUtility.ModifyPlayerLives;


public class WorldBorderHandler {
    static final String BORDER_TRACKER_OBJECTIVE = "borderTracker";
    static final int DAY_LENGTH =23000;
    //We cant have a config loaded during EntityJoinLevel, so we need to have this...
    //This is not good.
    @SubscribeEvent
    public void TickCounterHandler(TickEvent.ServerTickEvent event) {

        ServerLevel level = event.getServer().overworld();

        Config.WorldBorderMode mode = GameRuleHelper.GetWBMode(level);

        if (!Config.worldBorderExpands.get() || !(mode == Config.WorldBorderMode.Day || mode == Config.WorldBorderMode.Both)) return;



        Scoreboard board =event.getServer().getScoreboard();

        Objective objective = board.getObjective(BORDER_TRACKER_OBJECTIVE);

        Score scoreOnBoard = board.getOrCreatePlayerScore(BORDER_TRACKER_OBJECTIVE, objective);
        scoreOnBoard.setScore(scoreOnBoard.getScore() + 1);
        int scoreValue = board.getOrCreatePlayerScore(BORDER_TRACKER_OBJECTIVE, objective).getScore();
        int timeNeeded = DAY_LENGTH * GameRuleHelper.GetWBDaysBetweenExpansion(level);
        boolean dayPassed = (scoreValue > timeNeeded);


        if (!(dayPassed)) return;
        WorldBorder border= level.getWorldBorder();
        double size = border.getSize();
        size += GameRuleHelper.GetWBExpansionPerDay(level);
        int min = GameRuleHelper.GetWBMinSize(level);
        int max = GameRuleHelper.GetWBMaxSize(level);
        if ((min != -1 && size < min) || (max != -1 && size > max)) return;
        border.setSize(size);
        border.setSize(size);
        scoreOnBoard.setScore(0);

    }

    @SubscribeEvent
    public void PlayerLoginHandler(PlayerEvent.PlayerLoggedInEvent event){
        ServerLevel level=event.getEntity().getServer().overworld();
        LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,GameRuleHelper.SetWBSize(level));
        if (GameRuleHelper.SetWBSize(level)) {
            WorldBorderUtility.GenerateWorldBorder((ServerPlayer) event.getEntity(), level);
        }
        Scoreboard board = level.getScoreboard();
        if (!board.getObjectiveNames().contains(BORDER_TRACKER_OBJECTIVE)) {
            Objective newObjective =  board.addObjective(BORDER_TRACKER_OBJECTIVE, ObjectiveCriteria.DUMMY, Component.literal(BORDER_TRACKER_OBJECTIVE), ObjectiveCriteria.RenderType.INTEGER);
            board.getOrCreatePlayerScore(BORDER_TRACKER_OBJECTIVE,newObjective).setScore(GameRuleHelper.GetWBDaysBetweenExpansion(level));
        }
        /*for(EntityType t : ForgeRegistries.ENTITY_TYPES){
            LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,EntityType.getKey(t).toString());
        }*/
    }

    @SubscribeEvent
    public void PlayerLevelUpHandler(PlayerXpEvent.LevelChange event) {
        if (!Config.worldBorderExpands.get() &&
                (!(Config.worldBorderExpansionMode.get() == Config.WorldBorderMode.XP
                        || Config.worldBorderExpansionMode.get() == Config.WorldBorderMode.Both))) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = (ServerLevel) player.level();
        double size = level.getWorldBorder().getSize();
        size += GameRuleHelper.GetWBExpansionPerLevel(level) * event.getLevels();
        int min = GameRuleHelper.GetWBMinSize(level);
        int max = GameRuleHelper.GetWBMaxSize(level);
        if ((min != -1 && size < min) || (max != -1 && size > max)) return;
        level.getWorldBorder().setSize(size);

    }

    @SubscribeEvent
    public void WorldBorderDeathHandler(LivingDeathEvent event) {

        if ((event.getSource().getEntity() instanceof ServerPlayer) && Config.mobsThatExpandWBWhenKilled.get().contains(EntityType.getKey(event.getEntity().getType()).toString())) {
            ServerLevel level = (ServerLevel) event.getEntity().level();
            double size = level.getWorldBorder().getSize();
            size += GameRuleHelper.GetWBExpansionOnMobKill(level);
            int min = GameRuleHelper.GetWBMinSize(level);
            int max = GameRuleHelper.GetWBMaxSize(level);
            if ((min != -1 && size < min) || (max != -1 && size > max)) return;
            level.getWorldBorder().setSize(size);

        }
    }
}
