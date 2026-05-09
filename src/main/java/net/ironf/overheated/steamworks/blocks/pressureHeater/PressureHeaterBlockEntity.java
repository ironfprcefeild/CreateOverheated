package net.ironf.overheated.steamworks.blocks.pressureHeater;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.HeatDisplayType;
import net.ironf.overheated.utility.machines.CooledMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.heatTooltip;

public class PressureHeaterBlockEntity extends CooledMachineBlockEntity implements IHaveGoggleInformation {
    public PressureHeaterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    public int getFluidCapacity() {
        return 2000;
    }

    int timer = 75;
    HeatData recentReading = HeatData.empty();

    public int laserTimer = 0;
    public int laserHeatLevel = 0;
    ///IF a laser is firing into this, it will heat the steam in tank below to the same heat level
    /// as the highest level heat being inputted, using 1 of them.
    /// IF no laser is inputted, it harvests all heat from inputted steam.
    /// It can only harvest steam heat if it is cooled to 0 degrees or better, and gains
    /// lots of temperature when it processes steam.
    @Override
    public void tick() {
        super.tick();

        //Laser Check
        if (laserTimer > 0){
            laserTimer--;
        } else {
            laserHeatLevel = 0;
        }

        if (timer-- == 0){
            timer = 75;
            IFluidTank input = getOtherTank(Direction.DOWN);
            if (input == null || input.getFluidAmount() <= 10){
                recentReading = HeatData.empty();
                return;
            }
            if (laserHeatLevel > 0){
                steamHeatMode(input);
            }
        }
    }

    public void steamHeatMode(IFluidTank input){
        recentReading = HeatData.empty();
        int pressure = AllSteamFluids.getSteamPressure(input.getFluid());
        FluidStack toFill = AllSteamFluids.getSteamFromValues(pressure,laserHeatLevel,10);
        if (10 != Tank().fill(toFill, IFluidHandler.FluidAction.SIMULATE)){
            return;
        }
        addTemp((float) Math.floor(Math.pow(1.5,laserHeatLevel+1)));
        Tank().fill(toFill,IFluidHandler.FluidAction.EXECUTE);
        input.drain(10, IFluidHandler.FluidAction.EXECUTE);
    }

    public IFluidTank getOtherTank(Direction in){
        BlockEntity be = level.getBlockEntity(getBlockPos().relative(in));
        return be instanceof FluidTankBlockEntity ? ((FluidTankBlockEntity) be).getControllerBE().getTankInventory() : null;
    }

    public HeatData getRecentReading(){
        return recentReading;
    }

    //Cooling
    @Override public boolean doCooling() {return true;}
    @Override public boolean hasPassiveCooling() {return true;}
    @Override public CoolingData getPassiveCooling() {return new CoolingData(5f,0f);}

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip,isPlayerSneaking);
        if (laserHeatLevel > 0){
            heatTooltip(tooltip,new HeatData(laserHeatLevel == 1 ? 1 : 0, laserHeatLevel == 2 ? 1 : 0, laserHeatLevel == 3 ? 1 :0),HeatDisplayType.ABSORB);
        } else {
            heatTooltip(tooltip, recentReading, HeatDisplayType.SUPPLYING);
            tempAndCoolInfo(tooltip);
        }
        return true;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        timer = tag.getInt("timer");
        recentReading = HeatData.readTag(tag,"heat");
        laserTimer = tag.getInt("lasertimer");
        laserHeatLevel = tag.getInt("laserheatlevel");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",timer);
        HeatData.writeTag(tag,recentReading,"heat");
        tag.putInt("lasertimer",laserTimer);
        tag.putInt("laserheatlevel",laserHeatLevel);
    }


}
