package net.ironf.overheated.metalWorking;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;


public class TemperatureHandler {

    public static void changeTempAt(Level level, BlockPos pos, int amount, int heatLevel){
        if (level.getBlockState(pos).hasProperty(HeatedBlock.TEMPERATURE)){
            BlockState affectedState = level.getBlockState(pos);
            int newHeatLevel = Math.max(heatLevel,affectedState.getValue(HeatedBlock.HEATLEVEL));
            int oldTemp = affectedState.getValue(HeatedBlock.TEMPERATURE);
            affectedState.setValue(HeatedBlock.HEATLEVEL,newHeatLevel);
            affectedState.setValue(HeatedBlock.TEMPERATURE, Math.min(oldTemp+amount,newHeatLevel*8));
        }
    }


}
