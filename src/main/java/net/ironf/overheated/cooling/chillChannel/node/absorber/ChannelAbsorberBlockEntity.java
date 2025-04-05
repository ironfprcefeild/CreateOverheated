package net.ironf.overheated.cooling.chillChannel.node.absorber;

import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.cooling.chillChannel.network.IChillChannelHook;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChannelAbsorberBlockEntity extends SmartMachineBlockEntity implements IChillChannelHook {
    public ChannelAbsorberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //This block has no temperature of its own
    @Override
    public boolean doCooling() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- == 0){
            tickTimer = 20;
            if (!posSet) return;
            BlockEntity drawsFromEntity = level.getBlockEntity(drawsFromPos);
            if (drawsFromEntity instanceof IChillChannelHook hookEntity){
                //This channels draw point is still valid
                lastCapacity = hookEntity.getBlockCapacity() - 1;
                lastEff = hookEntity.getNetworkEfficency();
                lastMinTemp = hookEntity.getNetworkMinTemp();
                if (lastCapacity < 0 || lastEff == 0){
                    //Channel is invalid, marked by an efficiency of 0
                    invalidateNode();
                } else {
                    //Add the cooling units on the channel with the ones that this block is receiving.
                    lastCoolingUnits = hookEntity.getCoolingUnitsCapacity() + getCoolingUnits() * lastEff;
                }
            } else {
                //No hook to pull from
                invalidateNode();
            }
        }
    }

    void invalidateNode(){
        //We are invalid
        lastEff = 0f;
        lastCapacity = 0;
        lastCoolingUnits = 0;
        lastMinTemp = 10000;
    }

    BlockPos drawsFromPos;
    int tickTimer = 1;
    int lastCapacity;
    float lastEff;
    float lastMinTemp;
    float lastCoolingUnits;
    boolean posSet = false;
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("ticktimer");
        lastEff = tag.getFloat("eff");
        lastCapacity = tag.getInt("capacity");
        lastCoolingUnits = tag.getFloat("coolingunits");
        lastMinTemp = tag.getFloat("lastmintemp");
        drawsFromPos = BlockPos.of(tag.getLong("drawsfrom"));
        posSet = tag.getBoolean("posset");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag,clientPacket);
        tag.putInt("ticktimer",tickTimer);
        tag.putFloat("eff",lastEff);
        tag.putInt("capacity",lastCapacity);
        tag.putFloat("coolingunits",lastCoolingUnits);
        tag.putFloat("lastmintemp",lastMinTemp);
        tag.putLong("drawsfrom",drawsFromPos.asLong());
        tag.putBoolean("posset",posSet);
    }

    @Override
    public int getBlockCapacity() {
        return lastCapacity;
    }

    @Override
    public float getNetworkEfficency() {
        return lastEff;
    }

    @Override
    public float getNetworkMinTemp() {
        return lastMinTemp;
    }

    public float getCoolingUnitsCapacity() {
        return lastCoolingUnits;
    }

    @Override
    public void setHookTarget(BlockPos bp) {
        drawsFromPos = bp;
        posSet = true;
    }
}
