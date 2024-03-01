package net.ironf.overheated.steamworks.blocks.heatsink;

import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.AllBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;


public interface HeatSinkHelper {
    default float getHeatSunkenFrom(BlockPos pos, Level level){
        float runningTotal = 0;
        for (Direction d : Iterate.directions){
            BlockPos check = pos.relative(d);
            BlockState state = level.getBlockState(check);
            if (AllBlocks.HEAT_SINK.has(state) && state.getValue(BlockStateProperties.AXIS) == d.getAxis()){
                //We are at a heatsink facing the right way
                //The total sunk method will result in both the found heat sinks value, and all attached heatsinks
                runningTotal = runningTotal + ((HeatSinkBlockEntity) level.getBlockEntity(check)).findTotalSunk(d);
            }
        }
        return runningTotal;
    }
}
