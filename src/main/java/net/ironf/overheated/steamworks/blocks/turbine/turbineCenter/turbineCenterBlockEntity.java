package net.ironf.overheated.steamworks.blocks.turbine.turbineCenter;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class turbineCenterBlockEntity extends SmartBlockEntity {
    public turbineCenterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    //When initially sent passed radius should be 0, and turbine number should be 0
    public void progressSteamPacket(BlockPos startPoint,int pressureLevel, int radius, int turbineNumber){
        //This turbine is too big, we aren't doing anything
        if (turbineNumber > 12){
            return;
        }
        //Get some stuff and do some stuff
        BlockState myState = getMyState();
        BlockEntity faced = getFacedBE(myState);
        if (faced != null){
            boolean isEnd;
            if (faced.getType() == AllBlockEntities.TURBINE_CENTER.get()){
                isEnd = true;
            } else if (faced.getType() == AllBlockEntities.TURBINE_END.get()){
                isEnd = false;
            } else {
                return;
            }

            //Adjust Radius
            radius = Math.min(radius,findLocalRadius(myState));

            //Increment Turbine Number
            turbineNumber++;

            if (isEnd) {
                //We are add the end of a turbine, finish off the packet
                ((turbineEndBlockEntity) faced).handleEndOfPacket(startPoint,pressureLevel,radius,turbineNumber);
            } else {
                //We are in the middle of a turbine, send on to next one
                ((turbineCenterBlockEntity) faced).progressSteamPacket(startPoint,pressureLevel,radius,turbineNumber);
            }
        }
    }

    public BlockEntity getFacedBE(BlockState myState){
        return (level.getBlockEntity(getBlockPos().relative(myState.getValue(BlockStateProperties.HORIZONTAL_FACING))));
    }

    public BlockState getMyState(){
        return level.getBlockState(getBlockPos());
    }

    public int findLocalRadius(BlockState myState){
        Direction facing = myState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        int radiusRating = 1;

        //Find blockpos of cardinally adjacent blocks
        for (Direction d : Iterate.directions){
            if (d != facing && d != facing.getOpposite()){
                BlockPos bp = getBlockPos().relative(d);
                if (level.getBlockState(bp).getBlock() == AllBlocks.TURBINE_EXTENSION.get()){
                    radiusRating += 0.25;
                    for (Direction d2 : Iterate.directions){
                        if (level.getBlockState(bp.relative(d2)).getBlock() == AllBlocks.TURBINE_EXTENSION.get()){
                            radiusRating += 0.25;
                        }
                    }
                }
            }
        }

        return radiusRating;
    }
}
