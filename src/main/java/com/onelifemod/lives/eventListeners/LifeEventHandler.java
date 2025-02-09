package com.onelifemod.lives.eventListeners;

import com.onelifemod.*;
import com.onelifemod.common.GameRuleHelper;
import com.onelifemod.lives.LifeCommand;
import com.onelifemod.lives.LifeUtility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;
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

import static com.onelifemod.lives.LifeUtility.*;
import static com.onelifemod.lives.LifeUtility.TeamNames.*;

public class LifeEventHandler {


    @SubscribeEvent
    public void RegisterCMDS(RegisterCommandsEvent event){
        LifeCommand.register(event.getDispatcher());
    }


}
