package com.onelifemod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(onelifemod.MODID)
public class onelifemod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "onelifemod";
    // Directly reference a slf4j logger


    public onelifemod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(new LifeEventHandler());
        MinecraftForge.EVENT_BUS.register(new WorldBorderHandler());
        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        Config config = new Config(clientBuilder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, clientBuilder.build());

    }
}
