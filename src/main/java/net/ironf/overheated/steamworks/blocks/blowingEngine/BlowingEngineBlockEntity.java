package net.ironf.overheated.steamworks.blocks.blowingEngine;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.laserOptics.Diode.DiodeHeaters;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.ChatFormatting;
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

import static net.ironf.overheated.utility.GoggleHelper.addIndent;
import static net.ironf.overheated.utility.GoggleHelper.easyFloat;

public class BlowingEngineBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public BlowingEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /// Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour inputTank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

        behaviours.add(inputTank = SmartFluidTankBehaviour.single(this, 1000));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.inputTank.getPrimaryHandler());

    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyFluidHandler.invalidate();
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return inputTank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }

    /// Processing
    public int tickTimer = 20;
    public int lastOutputAmount = 0;
    public String errorMessage = "";
    public int lastHeatReading = 0;
    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- <= 0){

            int flyWheelSpeed = getFlyWheelSpeed();
            if (flyWheelSpeed == 0){
                errorMessage = "flywheel_not_spinning";
                tickTimer = 100;
                return;
            }
            //At Maximum speed, it takes a little less than 2 Steam Vents per Blowing Engine
            tickTimer = 1024 / flyWheelSpeed;

            int steamPressure = AllSteamFluids.getSteamPressure(inputTank.getPrimaryHandler().getFluid());
            if (steamPressure <= 0){
                errorMessage = "no_steam";
                return;
            }
            int steamHeating =  AllSteamFluids.getSteamHeat(inputTank.getPrimaryHandler().getFluid());

            IFluidTank outputTank = getTank(Direction.UP);
            if (outputTank == null){
                //No Output Tank
                errorMessage = "no_output_tank";
                return;
            }

            IFluidTank oxygenTank = getTank(Direction.DOWN);
            boolean oxyPresent = oxygenTank != null
                    && AllGasses.oxygen.SOURCE.get().isSame(oxygenTank.getFluid().getFluid())
                    && oxygenTank.getFluidAmount() >= 2;

            int heatingLevel = Math.max(
                        steamHeating,
                        (oxyPresent
                            ? HeatData.empty()
                            : DiodeHeaters.getActiveHeat(level,getBlockPos().below()))
                            .getHeatLevelOfHighest());
            lastHeatReading = heatingLevel;

            if (heatingLevel == 0){
                //No Heating
                errorMessage = "no_heating";
                return;
            }

            //Everything is good to go
            int fluidAmount = steamPressure+steamHeating*steamHeating;
            fluidAmount = oxyPresent ? fluidAmount * 3 : fluidAmount;
            FluidStack airCreated = new FluidStack(AllSteamFluids.HotAirs[heatingLevel-1],fluidAmount);

            if (outputTank.fill(airCreated, IFluidHandler.FluidAction.SIMULATE) == fluidAmount){
                outputTank.fill(airCreated, IFluidHandler.FluidAction.EXECUTE);
                inputTank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.EXECUTE);
                if (oxyPresent){
                    oxygenTank.drain(2, IFluidHandler.FluidAction.EXECUTE);
                }
                lastOutputAmount = fluidAmount;
                errorMessage = "";
            } else {
                errorMessage = "no_room_in_output";
            }
        }
    }

    public int getFlyWheelSpeed(){
        if (level.getBlockEntity(getBlockPos().relative(Direction.UP,2)) instanceof FlywheelBlockEntity FBE){
            return (int) Math.abs(FBE.getSpeed());
        } else {
            return 1;
        }
    }

    public IFluidTank getTank(Direction in){
        BlockPos pos = getBlockPos().relative(in);
        if (level.getBlockState(pos).getBlock() == AllBlocks.PRESSURIZED_CASING.get()) {pos = pos.relative(in);}

        BlockEntity be = level.getBlockEntity(pos);
        FluidTankBlockEntity tank = (be instanceof FluidTankBlockEntity) ? ((FluidTankBlockEntity) be).getControllerBE() : null;
        return (tank != null) ? tank.getTankInventory() : null;
    }


    /// Read/Write
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("ticktimer");
        errorMessage = tag.getString("error");
        lastHeatReading = tag.getInt("heatreading");
        lastOutputAmount = tag.getInt("outputamount");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("ticktimer",tickTimer);
        tag.putString("error",errorMessage);
        tag.putInt("heatreading",lastHeatReading);
        tag.putInt("outputamount",lastOutputAmount);
    }

    /// Goggles
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {

       if (errorMessage != "") {
           tooltip.add(GoggleHelper.addIndent(
                   Component.translatable("coverheated.blowing_engine.error." + errorMessage)));
           lastOutputAmount = 0;
           lastHeatReading = 0;
       }

        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        tooltip.add(addIndent(Component.translatable("coverheated.blowing_engine.heat").append(easyFloat(lastHeatReading)).withStyle(ChatFormatting.RED)));

        if (isPlayerSneaking) {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.blowing_engine.making")
                    .append(String.valueOf(lastOutputAmount)).append(Component.translatable("coverheated.blowing_engine.hot_air_in"))
                    .append(String.valueOf(tickTimer)).append(Component.translatable("coverheated.turbine.drain.ticks")),1));
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.blowing_engine.steam_vent_requires")
                    .append(easyFloat((float) (getFlyWheelSpeed() * 75) /10240))
                    .append(Component.translatable("coverheated.turbine.drain.steam_vent.to_run")),1));
             } else {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.tooltip.crouch_for_more_info"),1));
        }


        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
