package com.onelifemod;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public enum WorldBorderMode{
        XP,
        Day,
        Both
    }
    public static ForgeConfigSpec.ConfigValue<Boolean> worldBorderExpands ;
    public static ForgeConfigSpec.ConfigValue<Integer> worldBorderExpansionSizePerXPLevel;
    public static ForgeConfigSpec.ConfigValue<Integer> worldBorderExpansionSizePerDay;
    public static ForgeConfigSpec.ConfigValue<WorldBorderMode> worldBorderExpansionMode;
    public static ForgeConfigSpec.ConfigValue<Integer> startingWorldBorderSize;
    public static ForgeConfigSpec.ConfigValue<Integer> daysBetweenExpansion;
    public static ForgeConfigSpec.ConfigValue<Boolean> setWorldBorderSize;
    public static ForgeConfigSpec.ConfigValue<Integer> maxLives;
    public static ForgeConfigSpec.ConfigValue<Boolean> useLivesSystem;
    public static ForgeConfigSpec.ConfigValue<Boolean> allowGainingLivesThroughTamingAnimals;
    public static ForgeConfigSpec.ConfigValue<Boolean> livesSharedBetweenAllPlayers;
    public static ForgeConfigSpec.ConfigValue<Boolean> healthSharedBetweenAllPlayers;
    public  static ForgeConfigSpec.ConfigValue<Boolean> showNetherStarOnGainedLife;
    public static ForgeConfigSpec.ConfigValue<Integer> maxBorderSize;
    public static ForgeConfigSpec.ConfigValue<Integer> minBorderSize;
    public static ForgeConfigSpec.ConfigValue<Boolean> advancementsGiveLives;
    public static ForgeConfigSpec.ConfigValue<Boolean> damageCausesMaxHPLoss;

    public static ForgeConfigSpec.ConfigValue<Boolean> useHPLives;
    public static ForgeConfigSpec.ConfigValue<Boolean> hideLivesCounter;
    public static ForgeConfigSpec.ConfigValue<Boolean> randomizeGameSettings;
    public static ForgeConfigSpec.ConfigValue<Integer> maxRandomLifeAmount;
    public static ForgeConfigSpec.ConfigValue<Integer> minRandomLifeAmount;

    public static Boolean SetDefaultBorderSize(){
        return setWorldBorderSize.get()||worldBorderExpands.get();
    }

public static ForgeConfigSpec.ConfigValue<Boolean> showTeams;
    public static int GetMaxLives(){
        if(useHPLives.get()){
            return 10;
        }
        return maxLives.get();
    }
    public Config(ForgeConfigSpec.Builder builder){
        builder.comment("Default gamerule settings").push("general");
        builder.push("World border settings");
        setWorldBorderSize= builder.comment("Set world border size (is true if border expands):").define("SetWBSize",false);
        worldBorderExpands= builder.comment("World border expands:").define("WBExpands",false);
        worldBorderExpansionMode=builder.comment("World border expansion mode (XP/Day/Both)").define("WBEMode",WorldBorderMode.Day);
        worldBorderExpansionSizePerXPLevel =builder.comment("Expansion amount per level").define("WBExpansionPerLvl",4);
        worldBorderExpansionSizePerDay=builder.comment("Expansion amount per day").define("WBExpansionPerDay",1);
        daysBetweenExpansion=builder.comment("Days between expansions").define("WBExpansionDays",2);
        startingWorldBorderSize=builder.comment("Starting world border size").define("WBSize",16);
        maxBorderSize=builder.comment("Max world border size. -1 for off.").define("MaxWBSize",-1);
        minBorderSize=builder.comment("Min world border size. -1 for off.").define("MinWBSize",-1);
        builder.pop();
        builder.push("Life settings");
        useLivesSystem= builder.comment("Use lives system").define("UseLives",true);
        hideLivesCounter=builder.comment("Hide Lives Counter").define("HideLives",true);
        maxLives= builder.comment("Default amount of lives:").define("Default",5);
        livesSharedBetweenAllPlayers=builder.comment("All players share the same life count").define("LivesSharedAll",false);
        builder.push("Team settings");
        showTeams=builder.comment("Show life colors").define("ShowLifeColors",true);
        builder.pop();
        builder.push("Client-side Settings");
        showNetherStarOnGainedLife=builder.comment("Shows screen effect on gaining a life:").define("ShowNetherStarOnGain",true);
        builder.pop();

        builder.push("HP rules");
        useHPLives=builder.comment("Use the hearts on the health bar as lives.").define("UseHPLives",false);
        healthSharedBetweenAllPlayers=builder.comment("All players share the same HP").define("HPSharedAll",false);
        damageCausesMaxHPLoss=builder.comment("Losing any HP is permanent- until death.").define("HPLossIsForever",false);
        builder.pop();
        builder.push("Gaining Lives");
        allowGainingLivesThroughTamingAnimals=builder.comment("Allow players to gain lives through taming (increasing amounts) of animals").define("AllowNewLivesFromTaming",true);
        advancementsGiveLives=builder.comment("An increasing amount of advancements gives lives.").define("AdvancementGiveLives",true);
        builder.pop();
        builder.comment("Caution: The following settings will ignore any non-clientside setting!").push("RandomizerSettings");
        randomizeGameSettings=builder.comment("Randomize these settings per world. Ignores most of this file.").define("RandomizeSettings",true);
        maxRandomLifeAmount=builder.comment("The max amount of lives that can be randomly assigned").define("MaxRandLifeAmount",5);
        minRandomLifeAmount=builder.comment("The min amount of lives that can be randomly assigned").define("MinRandLifeAmount",1);
        builder.pop();
        builder.pop();
        builder.build();
    }
}
