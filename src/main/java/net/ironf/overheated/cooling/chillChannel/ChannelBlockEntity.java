package net.ironf.overheated.cooling.chillChannel;

import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.cooling.ICoolingBlockEntity;
import net.ironf.overheated.cooling.chillChannel.core.ChannelStatusBundle;
import net.ironf.overheated.cooling.chillChannel.expeller.ChannelExpellerBlockEntity;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ChannelBlockEntity extends SmartMachineBlockEntity {
    public ChannelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

     //Modifies channel state, and return the next blockpos to check
    float lastEfficiency = 1f;
    public BlockPos propagateChannel(ChannelStatusBundle status, float efficiency, float minTemp, MutableDirection channelMovingIn){
        status.addSource(getCoolingUnits()*efficiency);
        lastEfficiency = efficiency;
        channelMovingIn.setD(level.getBlockState(getBlockPos()).getValue(BlockStateProperties.FACING));
        return getBlockPos().relative(channelMovingIn.getImmutable());
    }

    //This allows Expellers to transfer


    @Override
    public CoolingData getCoolingDataFromDirection(BlockPos pos, Level level, Direction d) {
        BlockPos check = pos.relative(d);
        BlockEntity be = level.getBlockEntity(check);
        if (be instanceof ChannelExpellerBlockEntity cbe){
            return cbe.getGeneratedCoolingData(check,pos,level,d).setCoolingUnits(cbe.getCoolingUnits()/lastEfficiency);
        }
        if (be instanceof ICoolingBlockEntity){
            return (((ICoolingBlockEntity) be).getGeneratedCoolingData(check,pos,level,d));
        }
        return CoolingData.empty();
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

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        lastEfficiency = tag.getFloat("lasteff");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("lasteff",lastEfficiency);
    }
}

