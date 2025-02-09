package com.onelifemod.worldborders;

import com.onelifemod.Config;
import net.minecraft.world.level.GameRules;

public class WBRules {
    public static GameRules.Key<GameRules.BooleanValue> WBExpands;
    public static GameRules.Key<GameRules.BooleanValue> SetWBSize;
    public static GameRules.Key<GameRules.IntegerValue> WBEMode;
    public static GameRules.Key<GameRules.IntegerValue> WBExpansionPerLevel;
    public static GameRules.Key<GameRules.IntegerValue> WBExpansionPerDay;
    public static GameRules.Key<GameRules.IntegerValue> WBDaysBetweenExpansion;
    public static GameRules.Key<GameRules.IntegerValue> WBStartingSize;
    public static GameRules.Key<GameRules.IntegerValue> WBMaxSize;
    public static GameRules.Key<GameRules.IntegerValue> WBMinSize;

    public static GameRules.Key<GameRules.IntegerValue> WBExpansionPerKill;
    public static void register(){
        WBExpands=GameRules.register("WBExpands", GameRules.Category.MISC,GameRules.BooleanValue.create(Config.worldBorderExpands.get()));
        SetWBSize=GameRules.register("SetWBSize",GameRules.Category.MISC,GameRules.BooleanValue.create(Config.setWorldBorderSize.get()||Config.worldBorderExpands.get()));
        WBEMode=GameRules.register("WBEMode", GameRules.Category.MISC,GameRules.IntegerValue.create(Config.worldBorderExpansionMode.get().ordinal()));
        WBExpansionPerLevel=GameRules.register("WBExpansionPerLevel",GameRules.Category.MISC,GameRules.IntegerValue.create(Config.worldBorderExpansionSizePerXPLevel.get()));
        WBExpansionPerDay=GameRules.register("WBExpansionPerDay",GameRules.Category.MISC,GameRules.IntegerValue.create(Config.worldBorderExpansionSizePerDay.get()));
        WBDaysBetweenExpansion=GameRules.register("WBDaysBetweenExpansion",GameRules.Category.MISC,GameRules.IntegerValue.create(Config.daysBetweenExpansion.get()));
        WBStartingSize=GameRules.register("WBStartingSize",GameRules.Category.MISC,GameRules.IntegerValue.create(Config.startingWorldBorderSize.get()));
        WBMaxSize=GameRules.register("WBMaxSize",GameRules.Category.MISC,GameRules.IntegerValue.create(Config.maxBorderSize.get()));
        WBMinSize=GameRules.register("WBMinSize",GameRules.Category.MISC,GameRules.IntegerValue.create(Config.minBorderSize.get()));
        WBExpansionPerKill=GameRules.register("WBExpansionPerKill", GameRules.Category.MOBS,GameRules.IntegerValue.create(Config.wbExpansionPerKill.get()));
    }
}
