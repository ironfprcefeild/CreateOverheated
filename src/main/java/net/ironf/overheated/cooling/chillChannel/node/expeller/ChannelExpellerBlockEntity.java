package net.ironf.overheated.cooling.chillChannel.node.expeller;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.cooling.ICoolingBlockEntity;
import net.ironf.overheated.cooling.chillChannel.network.ChannelSlotBox;
import net.ironf.overheated.cooling.chillChannel.network.IChillChannelHook;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class ChannelExpellerBlockEntity extends SmartBlockEntity implements IChillChannelHook, ICoolingBlockEntity {
    public ChannelExpellerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    ScrollValueBehaviour capacityScrollWheel;
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        capacityScrollWheel =
                new ScrollValueBehaviour(Component.translatable("coverheated.chill_channel.expeller.scroll"), this, new ChannelSlotBox())
                        .between(1,512);

        behaviours.add(capacityScrollWheel);
    }



    //Cooling Stuff


    @Override
    public CoolingData getGeneratedCoolingData(BlockPos myPos, BlockPos cooledPos, Level level, Direction in) {
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        if (facing.getOpposite() == in
                && (level.getBlockState(cooledPos).getBlock() != AllBlocks.COOLER.get())
                && (level.getBlockState(cooledPos).getBlock() != AllBlocks.CHANNEL_ABSORBER.get())) {

            return lastCooler;
        } else {
            return CoolingData.empty();
        }
    }
    //Channel network stuff
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
                    //Subtract from channel based on how much the wheel is set too
                    lastCoolingUnits = hookEntity.getCoolingUnitsCapacity() - capacityScrollWheel.getValue();
                    if (lastCoolingUnits < 0){
                        //Too much drain, invalidate this node.
                        invalidateNode();
                    } else {
                        lastCooler.coolingUnits = capacityScrollWheel.getValue();
                        lastCooler.minTemp = lastMinTemp;
                    }
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
        lastCooler = CoolingData.empty();
    }

    BlockPos drawsFromPos;
    int tickTimer = 1;
    int lastCapacity;
    float lastEff;
    float lastCoolingUnits;
    float lastMinTemp;
    boolean posSet;
    CoolingData lastCooler;

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("ticktimer");
        lastEff = tag.getFloat("eff");
        lastCapacity = tag.getInt("capacity");
        lastCoolingUnits = tag.getFloat("coolingunits");
        lastMinTemp = tag.getFloat("lastmintemp");
        lastCooler = CoolingData.readTag(tag,"lastcooler");
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
        lastCooler.writeTag(tag,"lastcooler");
        tag.putLong("drawsfrom",drawsFromPos.asLong());
        tag.putBoolean("posset",posSet);

    }

    //Network Stuff
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
    }


}
