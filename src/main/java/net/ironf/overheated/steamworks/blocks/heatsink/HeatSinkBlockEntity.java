package net.ironf.overheated.steamworks.blocks.heatsink;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.AllBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class HeatSinkBlockEntity extends SmartBlockEntity {
    public HeatSinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    ////Doing Stuff
    //This method is used by the IHeatSinkReader interface to collect the total heat sink of a series of heat sinks
    public int findTotalSunk(Direction in){
        int runningTotal = findSunken();
        int i = 1;
        while (i != 5){
            BlockEntity be = level.getBlockEntity(getBlockPos().relative(in,i));
            if (be.getType() == AllBlockEntities.HEAT_SINK.get()){
                runningTotal = runningTotal + ((HeatSinkBlockEntity) be).findSunken();
            } else {
                return runningTotal;
            }
            i++;
        }
        return runningTotal;
    }

    public int findSunken(){
        return 0;
        //TODO make this method work
    }
}
