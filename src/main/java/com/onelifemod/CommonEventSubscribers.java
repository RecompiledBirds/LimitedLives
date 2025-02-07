package com.onelifemod;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import recompiled.core.LogUtils;

import org.apache.logging.log4j.Level;

public class CommonEventSubscribers {



    public static void DoCommonSetup(FMLCommonSetupEvent event){
        event.enqueueWork(()->{
           LogUtils.GetLogger(limitedLives.MOD_ID).log(Level.INFO,"Registering packets");
            LifeSimpleChannel.register();
        });
    }
}
