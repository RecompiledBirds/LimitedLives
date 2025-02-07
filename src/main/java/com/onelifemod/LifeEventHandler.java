package com.onelifemod;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;
import recompiled.core.LogUtils;
import recompiled.core.ScoreBoardUtils;
import recompiled.core.TagUtils;

import static com.onelifemod.LifeUtility.*;
import static com.onelifemod.LifeUtility.TeamNames.*;
import static net.minecraft.ChatFormatting.*;

public class LifeEventHandler {


    @SubscribeEvent(priority =EventPriority.HIGHEST)
    public void HandleDeath(LivingDeathEvent event){

        if(!(event.getEntity() instanceof  ServerPlayer p)||event.getSource()==null)return;

        int livesLeft = ModifyPlayerLives(p,-1);
        if (livesLeft == 0) {
            p.displayClientMessage(Component.literal("You are out of lives..."), false);
            p.setGameMode(GameType.SPECTATOR);
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT,p.level());
            bolt.moveTo(p.position());
            p.level().addFreshEntity(bolt);
            if(Config.livesSharedBetweenAllPlayers.get()){
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
        if(Config.useHPLives.get()){
            return;
        }

        p.displayClientMessage(Component.literal("You have " + livesLeft + " lives remaining..."), false);
    }
    @SubscribeEvent
    public void HandleLogin(PlayerEvent.PlayerLoggedInEvent event){
        ServerPlayer p = (ServerPlayer)event.getEntity();
        Scoreboard levelBoard = ScoreBoardUtils.GetOrSetScoreBoard(p);
        if(!levelBoard.hasObjective(objectiveName)){
            levelBoard.addObjective(objectiveName, ObjectiveCriteria.DUMMY, Component.literal(objectiveName), ObjectiveCriteria.RenderType.INTEGER);
        }
        Objective objective=levelBoard.getOrCreateObjective(objectiveName);
        if (FirstTimeConnection(p)){
            LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Doing first time connection work for "+p.getName());
            String name = LifeUtility.GetNameForBoard(p);

            levelBoard.getOrCreatePlayerScore(name, objective).setScore(Config.GetMaxLives());
            if(!Config.useHPLives.get()) {
                levelBoard.setDisplayObjective(1,objective);
            }

            if(Config.showTeams.get()) {
                levelBoard.addPlayerToTeam(name, GetTeam(levelBoard, Green));
            }
        }
    }
    @SubscribeEvent
    public  void HandleRespawn(PlayerEvent.PlayerRespawnEvent event){

        if (event.isEndConquered()) return;
        ServerPlayer p = (ServerPlayer)event.getEntity();

        if(Config.useHPLives.get()){
            p.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("health",2*(LifeUtility.GetLives(p)-Config.GetMaxLives()), AttributeModifier.Operation.ADDITION));
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
        if(Config.damageCausesMaxHPLoss.get()){
            p.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("health",-amount, AttributeModifier.Operation.ADDITION));
        }
        if(!Config.healthSharedBetweenAllPlayers.get()){return;}

        for(ServerPlayer otherPlayer : ScoreBoardUtils.AllPlayersOnServer(p.getServer())){
            if(otherPlayer==p)continue;
            if(Config.damageCausesMaxHPLoss.get()){
                otherPlayer.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("health",-amount, AttributeModifier.Operation.ADDITION));
            }
            p.displayClientMessage(Component.literal(otherPlayer.getName().toString()),false);
            otherPlayer.setHealth(p.getHealth()-amount);
        }
    }

    public void HandleAllHeal(ServerPlayer p,float amount){
        if(!Config.healthSharedBetweenAllPlayers.get()){return;}

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
