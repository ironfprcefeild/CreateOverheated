package net.ironf.overheated.cooling.chillChannel;

import net.ironf.overheated.cooling.chillChannel.core.ChannelStatusBundle;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ChannelBlockEntity extends SmartMachineBlockEntity {
    public ChannelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

     //Modifies channel state, and return the next blockpos to check
    public BlockPos propagateChannel(ChannelStatusBundle status, float efficiency, float minTemp, Direction channelMovingIn){
        status.addSource(getCoolingUnits()*efficiency);
        channelMovingIn = level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING);
        return getBlockPos().relative(channelMovingIn);
    }

    public void acceptNetwork(){}

    @Override
    public boolean hasPassiveCooling() {
        return false;
    }

    @Override
    public boolean doCooling() {
        return false;
    }
}
