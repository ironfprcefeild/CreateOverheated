package net.ironf.overheated.cooling;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

//Should only be applied to block entities
public interface ICoolingBlockEntity {

    default CoolingData getGeneratedCoolingData(BlockPos myPos, BlockPos cooledPos, Level level, Direction in){
        return new CoolingData(getGeneratedCoolingUnits(myPos,cooledPos,level,in),-5);
    }

    default float getGeneratedCoolingUnits(BlockPos myPos, BlockPos cooledPos, Level level, Direction in){
        return 0f;
    }
}
