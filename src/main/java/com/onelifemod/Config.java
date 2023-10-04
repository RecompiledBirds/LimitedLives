package com.onelifemod;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public enum WorldBorderMode{
        XP,
        Day,
        Both
    }
    public static ForgeConfigSpec.ConfigValue<Boolean> worldBorderExpands ;
    public static ForgeConfigSpec.ConfigValue<Integer> worldBorderExpansionSize;
    public static ForgeConfigSpec.ConfigValue<Integer> worldBorderExpansionSizePerDay;
    public static ForgeConfigSpec.ConfigValue<WorldBorderMode> worldBorderExpansionMode;
    public static ForgeConfigSpec.ConfigValue<Integer> startingWorldBorderSize;
    public static ForgeConfigSpec.ConfigValue<Integer> daysBetweenExpansion;
    public static ForgeConfigSpec.ConfigValue<Boolean> setWorldBorderSize;
    public static ForgeConfigSpec.ConfigValue<Integer> maxLives;
    public static ForgeConfigSpec.ConfigValue<Boolean> useLivesSystem;

    public static Boolean SetDefaultBorderSize(){
        return setWorldBorderSize.get()||worldBorderExpands.get();
    }

    public Config(ForgeConfigSpec.Builder builder){
        builder.comment("Settings:").push("general");
        builder.push("World border settings");
        setWorldBorderSize= builder.comment("Set world border size (is true if border expands:").define("SetWBSize",false);
        worldBorderExpands= builder.comment("World border expands:").define("WBExpands",false);
        worldBorderExpansionMode=builder.comment("World border expansion mode (XP/Day/Both)").define("WBEMode",WorldBorderMode.XP);
        worldBorderExpansionSize=builder.comment("Expansion amount (when using XP or per day").define("WBEAmount",4);
        worldBorderExpansionSizePerDay=builder.comment("Expansion amount (When using Both)").define("WBEAmountBothPerDay",1);
        daysBetweenExpansion=builder.comment("Days between expansions").define("WBExpansionDays",2);
        startingWorldBorderSize=builder.comment("Starting world border size").define("WBSize",16);
        builder.pop();
        builder.push("Life settings");
        setWorldBorderSize= builder.comment("Use lives system").define("UseLives",true);
        maxLives= builder.comment("Maximum amount of lives:").define("MaxLives",5);
        builder.pop();
        builder.build();
    }
}
