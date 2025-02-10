package com.onelifemod.lives;

import com.onelifemod.Config;
import net.minecraft.world.level.GameRules;

public class LifeRules {
    public static GameRules.Key<GameRules.BooleanValue> useLives;
    public static GameRules.Key<GameRules.BooleanValue> useHPLives;
    public static GameRules.Key<GameRules.BooleanValue> advancementsGiveLives;
    public static GameRules.Key<GameRules.BooleanValue> tamingGivesLives;
    public static GameRules.Key<GameRules.BooleanValue> damageCausesMaxHPLoss;
    public static GameRules.Key<GameRules.BooleanValue> allPlayersShareHP;
    public static GameRules.Key<GameRules.BooleanValue> showTeams;
    public static GameRules.Key<GameRules.BooleanValue> hideLivesCounter;
    public static GameRules.Key<GameRules.IntegerValue> maxOrStartingLives;
    public static GameRules.Key<GameRules.BooleanValue> allPlayersShareLives;
    public static void register(){
        useLives = GameRules.register("UseLives", GameRules.Category.PLAYER,GameRules.BooleanValue.create(Config.useLivesSystem.get()));
        useHPLives = GameRules.register("UseHPLives", GameRules.Category.PLAYER,GameRules.BooleanValue.create(Config.useHPLives.get()));
        advancementsGiveLives = GameRules.register("AdvancementsGiveLives", GameRules.Category.PLAYER,GameRules.BooleanValue.create(Config.advancementsGiveLives.get()));
        tamingGivesLives = GameRules.register("TamingGivesLives", GameRules.Category.PLAYER,GameRules.BooleanValue.create(Config.allowGainingLivesThroughTamingAnimals.get()));
        damageCausesMaxHPLoss = GameRules.register("DamageCausesMaxHPLoss", GameRules.Category.PLAYER,GameRules.BooleanValue.create(Config.damageCausesMaxHPLoss.get()));
        allPlayersShareHP = GameRules.register("AllPlayersShareHP", GameRules.Category.PLAYER,GameRules.BooleanValue.create(Config.healthSharedBetweenAllPlayers.get()));
        showTeams = GameRules.register("ShowTeams", GameRules.Category.PLAYER,GameRules.BooleanValue.create(Config.showTeams.get()));
        if(Config.useHPLives.get()) {
            maxOrStartingLives = GameRules.register("MaxOrStartingLives", GameRules.Category.PLAYER, GameRules.IntegerValue.create(10));
        }else {
            maxOrStartingLives = GameRules.register("MaxOrStartingLives", GameRules.Category.PLAYER, GameRules.IntegerValue.create(Config.maxLives.get()));
        }
        allPlayersShareLives= GameRules.register("ShareLives", GameRules.Category.PLAYER,GameRules.BooleanValue.create(Config.livesSharedBetweenAllPlayers.get()));
        hideLivesCounter= GameRules.register("HideLivesCounter", GameRules.Category.PLAYER,GameRules.BooleanValue.create(Config.hideLivesCounter.get()));
    }
}
