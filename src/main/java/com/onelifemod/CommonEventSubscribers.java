package com.onelifemod;

import com.onelifemod.lives.LifeRules;
import com.onelifemod.lives.LifeSimpleChannel;
import com.onelifemod.worldborders.WBRules;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import recompiled.core.LogUtils;

import org.apache.logging.log4j.Level;

public class CommonEventSubscribers {



    public static void DoCommonSetup(FMLCommonSetupEvent event){
        event.enqueueWork(()->{
           LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Registering packets");
            LifeSimpleChannel.register();
            LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Registering game rules");
            LifeRules.register();
            WBRules.register();
        });

    }
}
