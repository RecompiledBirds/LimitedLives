package com.onelifemod.common;

import com.onelifemod.Config;
import com.onelifemod.worldborders.WBRules;
import com.onelifemod.limitedLives;
import com.onelifemod.lives.LifeRules;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.Level;
import recompiled.core.LogUtils;

import java.util.Random;

public class GameRuleHelper {

    public static boolean ShowTeams(ServerLevel level){
        return GetRule(level,LifeRules.showTeams).get();
    }

    public static boolean UseLivesSystem(ServerLevel level){
        return GetRule(level,LifeRules.useLives).get();
    }

    public static boolean LivesSharedBetweenAllPlayers(ServerLevel level){
        return GetRule(level,LifeRules.allPlayersShareLives).get();
    }

    public static boolean UseHPLives(ServerLevel level){
        return GetRule(level,LifeRules.useHPLives).get();
    }
    public static int GetWBExpansionOnMobKill(ServerLevel level){
        return GetRule(level,WBRules.WBExpansionPerKill).get();
    }

    public static boolean HealthSharedBetweenAllPlayers(ServerLevel level){
        return GetRule(level,LifeRules.allPlayersShareHP).get();
    }

    public static boolean DamageCausesMaxHPLoss(ServerLevel level){
        return GetRule(level,LifeRules.damageCausesMaxHPLoss).get();
    }

    public static boolean AllowGainingLivesThroughTamingAnimals(ServerLevel level){
        return UseLivesSystem(level) && GetRule(level,LifeRules.tamingGivesLives).get();
    }

    public static boolean AdvancementsGiveLives(ServerLevel level){
        return GetRule(level,LifeRules.advancementsGiveLives).get();
    }
    public static int MaxLives(ServerLevel level){
        return  GetRule(level,LifeRules.maxOrStartingLives).get();
    }
    public static boolean HideLivesCounter(ServerLevel level){
        return GetRule(level,LifeRules.hideLivesCounter).get();
    }
    public static boolean SetWBSize(ServerLevel level){
        return GetRule(level,WBRules.SetWBSize).get()||GetRule(level,WBRules.WBExpands).get();
    }
    public static int GetWBDaysBetweenExpansion(ServerLevel level){
        return GetRule(level,WBRules.WBDaysBetweenExpansion).get();
    }
    public static int GetWBExpansionPerLevel(ServerLevel level){
        return GetRule(level, WBRules.WBExpansionPerLevel).get();
    }

    public static int GetWBMinSize(ServerLevel level){
        return GetRule(level, WBRules.WBMinSize).get();
    }
    public static int GetWBMaxSize(ServerLevel level){
        return GetRule(level,WBRules.WBMaxSize).get();
    }
    public static int GetWBExpansionPerDay(ServerLevel level){
        return GetRule(level,WBRules.WBExpansionPerDay).get();
    }
    public static <T extends GameRules.Value<T>> T GetRule(ServerLevel level, GameRules.Key<T> key){
        return level.getGameRules().getRule(key);
    }
    public static Config.WorldBorderMode GetWBMode(ServerLevel level){
        int val = level.getGameRules().getRule(WBRules.WBEMode).get();
        if(val<0){
            val=0;
        }
        else if(val>2){
            val=2;
        }
        switch (val){
            case 0-> {
                return Config.WorldBorderMode.XP;
            }
            case 1->{
                return Config.WorldBorderMode.Day;
            }
            default -> {
                return Config.WorldBorderMode.Both;
            }
        }
    }
    public static void RandomizeSettings(ServerLevel level, boolean alwaysShareLives){
        Random random = new Random();
        random.setSeed(level.getSeed());
        LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Running randomizer!");
        MinecraftServer server=level.getServer();
        GameRules rules=level.getGameRules();
        RandomizeMostLifeSettingsSettings(level,rules,server,random);
        if(!alwaysShareLives){
            rules.getRule(LifeRules.allPlayersShareLives).set(random.nextBoolean(),server);
        }
        if(!Config.chaosInABox.get())return;
        RandomizeWorldBorderRules(level,rules,server,random);

    }
    private static int RandomiseIntRule(GameRules rules, MinecraftServer server, Random random, String nameForLogging, GameRules.Key<GameRules.IntegerValue> key, int min, int max){
        int newSetting = random.nextInt(min,max+1);
        rules.getRule(key).set(newSetting,server);
        LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Setting "+nameForLogging+" "+newSetting);
        return newSetting;
    }
    private static Boolean RandomizeBooleanRule(GameRules rules, MinecraftServer server, Random random, String nameForLogging, GameRules.Key<GameRules.BooleanValue> key){
        boolean newSetting = random.nextBoolean();
        rules.getRule(key).set(newSetting,server);
        LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Setting "+nameForLogging+" "+newSetting);
        return newSetting;
    }
    public static void RandomizeMostLifeSettingsSettings(ServerLevel level, GameRules rules, MinecraftServer server, Random random){
        RandomizeBooleanRule(rules,server,random,"advancementsGiveLives",LifeRules.advancementsGiveLives);
        RandomizeBooleanRule(rules,server,random,"tamingGivesLives",LifeRules.tamingGivesLives);
        RandomizeBooleanRule(rules,server,random,"allPlayersShareHP",LifeRules.allPlayersShareHP);
        boolean useHPLives =RandomizeBooleanRule(rules,server,random,"useHPLives",LifeRules.useHPLives);
        RandomizeBooleanRule(rules,server,random,"showTeams",LifeRules.showTeams);
        RandomizeBooleanRule(rules,server,random,"damageCausesMaxHPLoss",LifeRules.damageCausesMaxHPLoss);
        RandomizeBooleanRule(rules,server,random,"hideLivesCounter",LifeRules.hideLivesCounter);
        if(useHPLives) {
            rules.getRule(LifeRules.maxOrStartingLives).set(random.nextInt(Config.minRandomLifeAmount.get()*2,Config.maxRandomLifeAmount.get()*2), server);
            return;
        }
        rules.getRule(LifeRules.maxOrStartingLives).set(random.nextInt(Config.minRandomLifeAmount.get(),Config.maxRandomLifeAmount.get()+1), server);
    }

    public static void RandomizeWorldBorderRules(ServerLevel level, GameRules rules, MinecraftServer server, Random random){
        RandomizeBooleanRule(rules,server,random,"WBExpands",WBRules.WBExpands);
        RandomiseIntRule(rules,server,random,"WBDaysBetweenExpansion",WBRules.WBDaysBetweenExpansion,1,10);
        int minSize =RandomiseIntRule(rules,server,random,"WBMinSize",WBRules.WBMinSize,1,16);
        int maxSize=16;
        if(random.nextInt(1,32)<5)
            maxSize =RandomiseIntRule(rules,server,random,"WBMaxSize",WBRules.WBMaxSize,minSize,minSize*30);
        else
            rules.getRule(WBRules.WBMaxSize).set(-1,server);
        RandomiseIntRule(rules,server,random,"WBStartingSize",WBRules.WBStartingSize,minSize,maxSize);
        RandomiseIntRule(rules,server,random,"WBEMode",WBRules.WBEMode,0,2);
        if(random.nextInt(1,32)>5)
            RandomiseIntRule(rules,server,random,"WBExpansionPerDay",WBRules.WBExpansionPerDay,-2,6);
        else
            RandomiseIntRule(rules,server,random,"WBExpansionPerDay",WBRules.WBExpansionPerDay,-3,3);
        RandomiseIntRule(rules,server,random,"WBExpansionPerLevel",WBRules.WBExpansionPerLevel,1,5);
    }

}
