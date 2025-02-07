package com.onelifemod;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.Level;
import recompiled.core.LogUtils;

import java.util.Random;

public class GameRuleHelper {
    public static boolean ShowTeams(ServerLevel level){
        return level.getGameRules().getRule(LifeRules.showTeams).get();
    }

    public static boolean UseLivesSystem(ServerLevel level){
        return level.getGameRules().getRule(LifeRules.useLives).get();
    }

    public static boolean LivesSharedBetweenAllPlayers(ServerLevel level){
        return level.getGameRules().getRule(LifeRules.allPlayersShareLives).get();
    }

    public static boolean UseHPLives(ServerLevel level){
        return level.getGameRules().getRule(LifeRules.useHPLives).get();
    }

    public static boolean HealthSharedBetweenAllPlayers(ServerLevel level){
        return level.getGameRules().getRule(LifeRules.allPlayersShareHP).get();
    }

    public static boolean DamageCausesMaxHPLoss(ServerLevel level){
        return level.getGameRules().getRule(LifeRules.damageCausesMaxHPLoss).get();
    }

    public static boolean AllowGainingLivesThroughTamingAnimals(ServerLevel level){
        return UseLivesSystem(level) && level.getGameRules().getRule(LifeRules.tamingGivesLives).get();
    }

    public static boolean AdvancementsGiveLives(ServerLevel level){;
        return level.getGameRules().getRule(LifeRules.advancementsGiveLives).get();
    }
    public static int MaxLives(ServerLevel level){
        return  level.getGameRules().getRule(LifeRules.maxOrStartingLives).get();
    }
    public static boolean HideLivesCounter(ServerLevel level){;
        return level.getGameRules().getRule(LifeRules.hideLivesCounter).get();
    }
    public static void RandomizeSettings(ServerLevel level, boolean alwaysShareLives, boolean worldBorderRandomizer){
        Random random = new Random();
        random.setSeed(level.getSeed());
        LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Running randomizer!");
        MinecraftServer server=level.getServer();
        GameRules rules=level.getGameRules();
        RandomizeMostLifeSettingsSettings(level,rules,server,random);
        if(!alwaysShareLives){
            rules.getRule(LifeRules.allPlayersShareLives).set(random.nextBoolean(),server);
        }


    }
    private static void RandomizeBooleanRule(ServerLevel level, GameRules rules, MinecraftServer server, Random random, String nameForLogging, GameRules.Key<GameRules.BooleanValue> key){
        Boolean newSetting = random.nextBoolean();
        rules.getRule(key).set(newSetting,server);
        LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Setting "+nameForLogging+" "+newSetting);
    }
    public static void RandomizeMostLifeSettingsSettings(ServerLevel level, GameRules rules, MinecraftServer server, Random random){
        RandomizeBooleanRule(level,rules,server,random,"advancementsGiveLives",LifeRules.advancementsGiveLives);
        RandomizeBooleanRule(level,rules,server,random,"tamingGivesLives",LifeRules.tamingGivesLives);
        RandomizeBooleanRule(level,rules,server,random,"allPlayersShareHP",LifeRules.allPlayersShareHP);
        RandomizeBooleanRule(level,rules,server,random,"useHPLives",LifeRules.useHPLives);
        RandomizeBooleanRule(level,rules,server,random,"showTeams",LifeRules.showTeams);
        RandomizeBooleanRule(level,rules,server,random,"damageCausesMaxHPLoss",LifeRules.damageCausesMaxHPLoss);
        RandomizeBooleanRule(level,rules,server,random,"hideLivesCounter",LifeRules.hideLivesCounter);
        rules.getRule(LifeRules.maxOrStartingLives).set(random.nextInt(Config.minRandomLifeAmount.get(),Config.maxRandomLifeAmount.get()+1), server);
    }

}
