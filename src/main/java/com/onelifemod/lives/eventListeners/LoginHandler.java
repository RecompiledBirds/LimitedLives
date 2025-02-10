package com.onelifemod.lives.eventListeners;

import com.onelifemod.Config;
import com.onelifemod.common.GameRuleHelper;
import com.onelifemod.limitedLives;
import com.onelifemod.lives.LifeUtility;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;
import recompiled.core.LogUtils;
import recompiled.core.ScoreBoardUtils;

import java.util.Optional;
import java.util.stream.Stream;

import static com.onelifemod.lives.LifeUtility.*;
import static com.onelifemod.lives.LifeUtility.TeamNames.Green;

public class LoginHandler {
    @SubscribeEvent
    public void HandleLogin(PlayerEvent.PlayerLoggedInEvent event){

        ServerPlayer p = (ServerPlayer)event.getEntity();
        Scoreboard levelBoard = ScoreBoardUtils.GetOrSetScoreBoard(p);
        if(p.getServer()==null)return;
        ServerLevel level = p.getServer().overworld();
        if(Config.randomizeGameSettings.get()&&!levelBoard.hasObjective("LLM.Randomized")){
            GameRuleHelper.RandomizeSettings((level),true);
            levelBoard.addObjective("LLM.Randomized", ObjectiveCriteria.DUMMY, Component.literal("LLM.Randomized"), ObjectiveCriteria.RenderType.INTEGER);
        }
        //  Registries.WORLD_PRESET.registry()
       //WorldPresets.class

        LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO, Registries.WORLD_PRESET.location().toString());
        Objective objective=ScoreBoardUtils.GetOrRessuringlyCreateObjective(levelBoard,objectiveName);
        if (FirstTimeConnection(p)){
            LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Doing first time connection work for "+p.getName());
            String name = LifeUtility.GetNameForBoard(p);

            levelBoard.getOrCreatePlayerScore(name, objective).setScore(GameRuleHelper.MaxLives(level));
            if(GameRuleHelper.UseHPLives(level)){
                p.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("health",2*(LifeUtility.GetLives(p)-10), AttributeModifier.Operation.ADDITION));
            }
            if(!GameRuleHelper.UseHPLives(p.serverLevel())&&!GameRuleHelper.HideLivesCounter(level)) {
                levelBoard.setDisplayObjective(1,objective);
            }

            if(GameRuleHelper.ShowTeams(level)) {
                levelBoard.addPlayerToTeam(name, GetTeam(levelBoard, Green));
            }
        }
    }
}
