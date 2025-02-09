package com.onelifemod.lives.eventListeners;

import com.onelifemod.Config;
import com.onelifemod.common.GameRuleHelper;
import com.onelifemod.lives.LifeUtility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import recompiled.core.ScoreBoardUtils;

import static com.onelifemod.lives.LifeUtility.ModifyPlayerLives;

public class LivesDeathHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void HandleDeath(LivingDeathEvent event){
        if(!(event.getEntity().level() instanceof ServerLevel level))return;
        if(!GameRuleHelper.UseLivesSystem(level))return;
        if(event.getSource()!=null)return;

        if((event.getSource().getEntity() instanceof ServerPlayer killPlayer) && Config.mobsThatGiveLifeWhenKilled.get().contains(EntityType.getKey(event.getEntity().getType()).toString())){
            ModifyPlayerLives(killPlayer,1);
        }
        if(!(event.getEntity() instanceof  ServerPlayer p))return;

        int livesLeft = ModifyPlayerLives(p,-1);
        if (livesLeft == 0) {
            p.displayClientMessage(Component.literal("You are out of lives..."), false);
            p.setGameMode(GameType.SPECTATOR);
            SpawnLightingBoltAtPlayer(p);
            if(p.getServer()!=null && GameRuleHelper.LivesSharedBetweenAllPlayers(p.serverLevel())){
                for(ServerPlayer otherPlayer : ScoreBoardUtils.AllPlayersOnServer(p.getServer())){
                    if(otherPlayer==p)continue;
                    otherPlayer.kill();
                    SpawnLightingBoltAtPlayer(otherPlayer);
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
    private void SpawnLightingBoltAtPlayer(ServerPlayer player){
        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT,player.level());
        bolt.moveTo(player.position());
        player.level().addFreshEntity(bolt);
    }




    @SubscribeEvent
    public  void HandleRespawn(PlayerEvent.PlayerRespawnEvent event){

        if (event.isEndConquered()) return;
        ServerPlayer p = (ServerPlayer)event.getEntity();
        if(p==null)return;
        if(GameRuleHelper.UseHPLives(p.serverLevel())){
            p.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("health",2*(LifeUtility.GetLives(p)- 10), AttributeModifier.Operation.ADDITION));
        }
        if(p.isSpectator()&&LifeUtility.GetLives(p)>0){
            p.setGameMode(GameType.SURVIVAL);
        }
    }
}
