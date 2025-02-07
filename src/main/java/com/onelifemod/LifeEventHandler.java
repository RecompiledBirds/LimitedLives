package com.onelifemod;

import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Level;
import recompiled.core.LogUtils;
import recompiled.core.ScoreBoardUtils;

import static com.onelifemod.LifeUtility.*;
import static com.onelifemod.LifeUtility.TeamNames.*;

public class LifeEventHandler {


    @SubscribeEvent(priority =EventPriority.HIGHEST)
    public void HandleDeath(LivingDeathEvent event){

        if(!(event.getEntity() instanceof  ServerPlayer p)||event.getSource()==null)return;
        ServerLevel level = p.serverLevel();
        int livesLeft = ModifyPlayerLives(p,-1);
        if (livesLeft == 0) {
            p.displayClientMessage(Component.literal("You are out of lives..."), false);
            p.setGameMode(GameType.SPECTATOR);
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT,level);
            bolt.moveTo(p.position());
            p.level().addFreshEntity(bolt);
            if(GameRuleHelper.LivesSharedBetweenAllPlayers(p.serverLevel())){
                for(ServerPlayer otherPlayer : ScoreBoardUtils.AllPlayersOnServer(p.getServer())){
                    if(otherPlayer==p)continue;
                    otherPlayer.kill();
                    bolt = new LightningBolt(EntityType.LIGHTNING_BOLT,otherPlayer.level());
                    bolt.moveTo(otherPlayer.position());
                    otherPlayer.level().addFreshEntity(bolt);
                    otherPlayer.setGameMode(GameType.SPECTATOR);
                    otherPlayer.displayClientMessage(Component.literal("You are out of lives..."), false);
                }
            }
            return;
        }
        if(GameRuleHelper.UseHPLives(level)){
            return;
        }
        if(!GameRuleHelper.HideLivesCounter(level))
            p.displayClientMessage(Component.literal("You have " + livesLeft + " lives remaining..."), false);
    }
    @SubscribeEvent
    public void HandleLogin(PlayerEvent.PlayerLoggedInEvent event){

        ServerPlayer p = (ServerPlayer)event.getEntity();
        Scoreboard levelBoard = ScoreBoardUtils.GetOrSetScoreBoard(p);
        if(!levelBoard.hasObjective(objectiveName)){
            levelBoard.addObjective(objectiveName, ObjectiveCriteria.DUMMY, Component.literal(objectiveName), ObjectiveCriteria.RenderType.INTEGER);
        }
        ServerLevel level = p.serverLevel();
        if(Config.randomizeGameSettings.get()&&!levelBoard.hasObjective("LLM.Randomized")){
            GameRuleHelper.RandomizeSettings((level),true,false);
            levelBoard.addObjective("LLM.Randomized", ObjectiveCriteria.DUMMY, Component.literal("LLM.Randomized"), ObjectiveCriteria.RenderType.INTEGER);
        }
        Objective objective=levelBoard.getOrCreateObjective(objectiveName);
        if (FirstTimeConnection(p)){
            LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Doing first time connection work for "+p.getName());
            String name = LifeUtility.GetNameForBoard(p);
            if(GameRuleHelper.UseHPLives(level)){
                p.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("health",2*(LifeUtility.GetLives(p)-10), AttributeModifier.Operation.ADDITION));
            }
            levelBoard.getOrCreatePlayerScore(name, objective).setScore(GameRuleHelper.MaxLives(level));
            if(!GameRuleHelper.UseHPLives(p.serverLevel())&&!GameRuleHelper.HideLivesCounter(level)) {
                levelBoard.setDisplayObjective(1,objective);
            }

            if(GameRuleHelper.ShowTeams(level)) {
                levelBoard.addPlayerToTeam(name, GetTeam(levelBoard, Green));
            }
        }
    }
    @SubscribeEvent
    public  void HandleRespawn(PlayerEvent.PlayerRespawnEvent event){

        if (event.isEndConquered()) return;
        ServerPlayer p = (ServerPlayer)event.getEntity();

        if(GameRuleHelper.UseHPLives(p.serverLevel())){
            p.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("health",2*(LifeUtility.GetLives(p)- 10), AttributeModifier.Operation.ADDITION));
        }
        if(p.isSpectator()&&LifeUtility.GetLives(p)>0){
            p.setGameMode(GameType.SURVIVAL);
        }
    }

    @SubscribeEvent
    public void HandleLivingHurtEvent(LivingHurtEvent event){
        if(!(event.getEntity() instanceof  ServerPlayer player))return;
        HandleAllHurt(player,event.getAmount());
    }
    @SubscribeEvent
    public void HandleLivingHealEvent(LivingHealEvent event){
        if(!(event.getEntity() instanceof  ServerPlayer player))return;
        HandleAllHeal(player,event.getAmount());
    }

    public void HandleAllHurt(ServerPlayer p,float amount){
        if(GameRuleHelper.DamageCausesMaxHPLoss(p.serverLevel())){
            p.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("health",-amount, AttributeModifier.Operation.ADDITION));
        }
        if(!GameRuleHelper.HealthSharedBetweenAllPlayers(p.serverLevel())){return;}
        for(ServerPlayer otherPlayer : ScoreBoardUtils.AllPlayersOnServer(p.getServer())){
            if(otherPlayer==p)continue;
            if(GameRuleHelper.DamageCausesMaxHPLoss(p.serverLevel())){
                otherPlayer.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("health",-amount, AttributeModifier.Operation.ADDITION));
            }
            p.displayClientMessage(Component.literal(otherPlayer.getName().toString()),false);
            otherPlayer.setHealth(p.getHealth()-amount);
        }
    }

    public void HandleAllHeal(ServerPlayer p,float amount){
        if(!GameRuleHelper.HealthSharedBetweenAllPlayers(p.serverLevel())){return;}

        for(ServerPlayer otherPlayer : ScoreBoardUtils.AllPlayersOnServer(p.getServer())){
            if(otherPlayer==p)continue;
            p.displayClientMessage(Component.literal(otherPlayer.getName().toString()),false);
            otherPlayer.setHealth(p.getHealth()+amount);
        }
    }


    @SubscribeEvent
    public void RegisterCMDS(RegisterCommandsEvent event){
        GiveLife.register(event.getDispatcher());
    }


}
