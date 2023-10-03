package com.onelifemod;
import com.electronwill.nightconfig.core.utils.ConfigWrapper;
import net.minecraftforge.common.ForgeConfigSpec;
public class Config {
    public static ForgeConfigSpec.ConfigValue<Integer> maxLives;
    public Config(ForgeConfigSpec.Builder builder){
        builder.comment("Settings:").push("general");
        maxLives= builder.comment("Maximum amount of lives:").define("max lives",5);
        builder.pop();
        builder.build();
    }
}
