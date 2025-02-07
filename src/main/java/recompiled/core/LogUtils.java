package recompiled.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtils {
    public static Logger GetLogger(String modid){
        return LogManager.getLogger(modid);
    }
}
