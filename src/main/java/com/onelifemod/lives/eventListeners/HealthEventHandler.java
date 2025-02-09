package com.onelifemod.lives.eventListeners;

import com.onelifemod.common.GameRuleHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import recompiled.core.ScoreBoardUtils;

public class HealthEventHandler {

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
}
