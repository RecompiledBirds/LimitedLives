package com.onelifemod;
import com.electronwill.nightconfig.core.utils.ConfigWrapper;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraftforge.common.ForgeConfigSpec;

import java.security.PublicKey;

public class Config {
    public static enum WorldBorderMode{
        XP,
        Day,
        Both
    }
    public static ForgeConfigSpec.ConfigValue<Boolean> worldBorderExpands ;
    public static ForgeConfigSpec.ConfigValue<Integer> worldBorderExpansionSize;
    public static ForgeConfigSpec.ConfigValue<Integer> worldBorderExpansionSizePerDay;
    public static ForgeConfigSpec.ConfigValue<WorldBorderMode> worldBorderExpansionMode;
    public static ForgeConfigSpec.ConfigValue<Integer> maxLives;
    public Config(ForgeConfigSpec.Builder builder){
        builder.comment("Settings:").push("general");
        maxLives= builder.comment("Maximum amount of lives:").define("max lives",5);
        builder.push("World border settings");
        worldBorderExpands= builder.comment("World border expands:").define("world border expands",false);
        worldBorderExpansionMode=builder.comment("World border expansion mode (XP/Day/Both)").define("WB expansion mode",WorldBorderMode.XP);
        worldBorderExpansionSize=builder.comment("Expansion amount").define("WB expansion amount (when using only XP or per day):",4);
        worldBorderExpansionSizePerDay=builder.comment("Expansion amount").define("WB expansion amount per day(When using Both)",1);
        builder.pop();
        builder.build();
    }
}
