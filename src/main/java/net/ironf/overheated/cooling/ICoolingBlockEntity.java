package net.ironf.overheated.cooling;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.tools.Diagnostic;

//Should only be applied to block entities
public interface ICoolingBlockEntity {

    default CoolingData getCoolingData(BlockPos myPos, BlockPos cooledPos, Level level, Direction in){
        return new CoolingData(getCoolingUnits(myPos,cooledPos,level,in),-5);
    }

    default float getCoolingUnits(BlockPos myPos, BlockPos cooledPos, Level level, Direction in){
        return 0f;
    }
}
