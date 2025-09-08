package net.ironf.overheated.steamworks.blocks.pressureHeater;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.HeatDisplayType;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;
import static net.ironf.overheated.utility.GoggleHelper.heatTooltip;

public class PressureHeaterBlockEntity extends SmartMachineBlockEntity implements IHaveGoggleInformation {
    public PressureHeaterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 2000).forbidInsertion());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.tank.getPrimaryHandler());

    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }
        return super.getCapability(cap, side);
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
            IFluidTank input = getTank(Direction.DOWN);
            if (input == null || input.getFluidAmount() >= 10){
                recentReading = HeatData.empty();
                return;
            }
            if (laserHeatLevel > 0){
                steamHeatMode(input);
            } else {
                steamCoolMode(input);
            }

        }
    }

    public void steamCoolMode(IFluidTank input){
        int readHeat = AllSteamFluids.getSteamHeat(input.getFluid());
        if (readHeat > 0 && currentTemp <= 0) {
            int pressure = AllSteamFluids.getSteamPressure(input.getFluid());
            FluidStack toFill = AllSteamFluids.getSteamFromValues(pressure,0,10);
            if (10 != tank.getPrimaryHandler().fill(toFill, IFluidHandler.FluidAction.SIMULATE)){
                return;
            }
            recentReading = new HeatData(readHeat == 1 ? 1 : 0, readHeat == 2 ? 1 : 0, readHeat == 3 ? 1 : 0);
            tank.getPrimaryHandler().fill(AllSteamFluids.getSteamFromValues(pressure,0,10), IFluidHandler.FluidAction.EXECUTE);
            addTemp((float) Math.pow(4,2*readHeat-1));
            input.drain(10, IFluidHandler.FluidAction.EXECUTE);
        } else {
            recentReading = HeatData.empty();
        }
    }
    public void steamHeatMode(IFluidTank input){
        recentReading = HeatData.empty();
        int pressure = AllSteamFluids.getSteamPressure(input.getFluid());
        FluidStack toFill = AllSteamFluids.getSteamFromValues(pressure,laserHeatLevel,10);
        if (10 != tank.getPrimaryHandler().fill(toFill, IFluidHandler.FluidAction.SIMULATE)){
            return;
        }
        tank.getPrimaryHandler().fill(toFill,IFluidHandler.FluidAction.EXECUTE);
        input.drain(10, IFluidHandler.FluidAction.EXECUTE);
    }

    public IFluidTank getTank(Direction in){
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
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
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
