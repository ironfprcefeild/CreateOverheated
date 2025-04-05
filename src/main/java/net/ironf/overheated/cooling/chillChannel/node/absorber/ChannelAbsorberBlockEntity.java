package net.ironf.overheated.cooling.chillChannel.node.absorber;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.cooling.chillChannel.network.IChillChannelHook;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ChannelAbsorberBlockEntity extends SmartMachineBlockEntity implements IChillChannelHook, IHaveGoggleInformation {
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
                sendParticlesToo(drawsFromPos,this);
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
        posSet = tag.getBoolean("posset");
        drawsFromPos = posSet ? BlockPos.of(tag.getLong("drawsfrom")) : null;

    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag,clientPacket);
        tag.putInt("ticktimer",tickTimer);
        tag.putFloat("eff",lastEff);
        tag.putInt("capacity",lastCapacity);
        tag.putFloat("coolingunits",lastCoolingUnits);
        tag.putFloat("lastmintemp",lastMinTemp);
        tag.putBoolean("posset",posSet);
        if (posSet) {
            tag.putLong("drawsfrom", drawsFromPos.asLong());
        }
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

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(posSet ? Component.translatable("coverheated.chill_channel.node.draws_from").append(drawsFromPos.toString())
                : Component.translatable("coverheated.chill_channel.node.unset"));
        tooltip.add(Component.translatable("coverheated.chill_channel.cooling_units").append(String.valueOf(lastCoolingUnits)));
        tooltip.add(Component.translatable("coverheated.chill_channel.mintemp").append(String.valueOf(lastMinTemp)));
        tooltip.add(Component.translatable("coverheated.chill_channel.eff").append(String.valueOf(lastEff)));
        tooltip.add(Component.translatable("coverheated.chill_channel.capacity").append(String.valueOf(lastCapacity)));
        return true;
    }
}
