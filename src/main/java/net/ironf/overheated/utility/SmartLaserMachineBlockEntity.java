package net.ironf.overheated.utility;

import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public abstract class SmartLaserMachineBlockEntity extends SmartMachineBlockEntity implements ILaserAbsorber {
    public SmartLaserMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void fifthTick() {
        super.fifthTick();
        totalLaserHeat = HeatData.empty();
        for (Direction d : Iterate.directions){
            laserTick(d);
            totalLaserHeat = HeatData.mergeHeats(totalLaserHeat,laserHeats.get(d));
        }
    }


    public HeatData totalLaserHeat = HeatData.empty();

    HashMap<Direction,Integer> laserTimers = new HashMap<>();
    HashMap<Direction,HeatData> laserHeats = new HashMap<>();
    @Override
    public void setLaserTimer(int laserTimer,Direction d) {
        this.laserTimers.put(d,laserTimer);
    }
    @Override
    public void setLaserHD(HeatData hd,Direction d) {
        this.laserHeats.put(d,hd);
    }
    @Override
    public int getLaserTimer(Direction d) {
        return laserTimers.getOrDefault(d,0);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        totalLaserHeat = HeatData.readTag(tag,"total_laser_heat");
        laserHeats.clear(); laserTimers.clear();
        for (Direction d : Iterate.directions){
            laserHeats.put(d,HeatData.readTag(tag,d.name()+"_laser_heat"));
            laserTimers.put(d,tag.getInt(d.name()+"_laser_timer"));
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        totalLaserHeat.writeTag(tag,"total_laser_heat");
        for (Direction d : Iterate.directions){
            tag.putInt(d.name()+"_laser_timer", laserTimers.getOrDefault(d,0));
            laserHeats.getOrDefault(d, HeatData.empty()).writeTag(tag,d.name()+"_laser_heat");
        }
    }
}
