package com.onelifemod;

import com.onelifemod.lives.LifeRules;
import com.onelifemod.lives.LifeSimpleChannel;
import com.onelifemod.worldborders.WBRules;
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
