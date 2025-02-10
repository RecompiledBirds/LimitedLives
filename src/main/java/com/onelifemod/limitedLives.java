package com.onelifemod;

import com.onelifemod.lives.eventListeners.*;
import com.onelifemod.worldborders.WorldBorderHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.spongepowered.asm.mixin.Mixin;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(limitedLives.MOD_ID)
public class limitedLives {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "limitedlives";


    public limitedLives() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonEventSubscribers::DoCommonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(new LifeEventHandler());
        MinecraftForge.EVENT_BUS.register(new PositiveLifeEventHandler());
        MinecraftForge.EVENT_BUS.register(new HealthEventHandler());
        MinecraftForge.EVENT_BUS.register(new LivesDeathHandler());
        MinecraftForge.EVENT_BUS.register(new LoginHandler());
        MinecraftForge.EVENT_BUS.register(new WorldBorderHandler());
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();


        Config config = new Config(clientBuilder);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, clientBuilder.build());

    }


}
