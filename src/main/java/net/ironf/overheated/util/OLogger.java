package net.ironf.overheated.util;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;


public class OLogger {



    public static void LogPos(BlockPos toLog, Logger log){
        log.info("X: " + toLog.getX() + ", Y: " + toLog.getY() + ", Z: " + toLog.getZ());
    }
}
