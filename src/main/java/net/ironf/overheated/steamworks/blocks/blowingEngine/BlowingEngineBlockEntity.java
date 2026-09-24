package net.ironf.overheated.steamworks.blocks.blowingEngine;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.laserOptics.Diode.DiodeHeaters;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.machines.CooledMachineBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;
import static net.ironf.overheated.utility.GoggleHelper.easyFloat;

public class BlowingEngineBlockEntity extends CooledMachineBlockEntity implements IHaveGoggleInformation {
    public BlowingEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /// Fluid Handling

    @Override
    public int getFluidCapacity() {
        return 1000;
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
            if (currentTemp >= 50){
                errorMessage ="too_hot";
                return;
            }
            //At Maximum speed, it takes a little less than 2 Steam Vents per Blowing Engine
            tickTimer = 1024 / flyWheelSpeed;

            int steamPressure = AllSteamFluids.getSteamPressure(Tank().getFluid());
            if (steamPressure <= 0){
                errorMessage = "no_steam";
                return;
            }
            int steamHeating =  AllSteamFluids.getSteamHeat(Tank().getFluid());

            IFluidTank outputTank = getOtherTank(Direction.UP);
            if (outputTank == null){
                //No Output Tank
                errorMessage = "no_output_tank";
                return;
            }

            IFluidTank oxygenTank = getOtherTank(Direction.DOWN);
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

            //Atmospheric Extraction
            Fluid alternateFluid = null;
            if (level.getBlockState(getBlockPos().below(oxyPresent ? 2 : 1)).is(AllBlocks.INTAKE_FILTER.get())){
                alternateFluid = switch (level.dimension().toString()){
                    case "ResourceKey[minecraft:dimension / minecraft:overworld]" ->
                        heatingLevel >= 1 ? AllGasses.nitrogen.SOURCE.get().getSource() : null;
                    case "ResourceKey[minecraft:dimension / minecraft:the_nether]" ->
                            heatingLevel >= 2 ? AllGasses.cinderfume.SOURCE.get().getSource() : null;
                    case "ResourceKey[minecraft:dimension / minecraft:the_end]" ->
                            heatingLevel >= 3 ? AllGasses.voidaium.SOURCE.get().getSource() : null;
                    default -> null;
                };
                if (alternateFluid != null){
                    addTemp((float) Math.pow(3,heatingLevel+1));
                }
            }

            //Everything is good to go
            int fluidAmount = steamPressure+steamHeating*steamHeating;
            fluidAmount = oxyPresent ? fluidAmount * 3 : fluidAmount;
            FluidStack airCreated = new FluidStack(
                    alternateFluid == null ? AllSteamFluids.HotAirs[heatingLevel-1] : alternateFluid,fluidAmount);

            if (outputTank.fill(airCreated, IFluidHandler.FluidAction.SIMULATE) == fluidAmount){
                outputTank.fill(airCreated, IFluidHandler.FluidAction.EXECUTE);
                Tank().drain(1, IFluidHandler.FluidAction.EXECUTE);
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

    public IFluidTank getOtherTank(Direction in){
        BlockPos pos = getBlockPos().relative(in);
        if (level.getBlockState(pos).getBlock() == AllBlocks.PRESSURIZED_CASING.get()) {pos = pos.relative(in);}

        BlockEntity be = level.getBlockEntity(pos);
        FluidTankBlockEntity tank = (be instanceof FluidTankBlockEntity) ? ((FluidTankBlockEntity) be).getControllerBE() : null;
        return (tank != null) ? tank.getTankInventory() : null;
    }

    /// Cooling
    @Override
    public boolean doCooling() {
        return true;
    }

    @Override
    public CoolingData getPassiveCooling() {
        return new CoolingData(4f,0f);
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

       super.addToGoggleTooltip(tooltip,isPlayerSneaking);
        tooltip.add(addIndent(Component.translatable("coverheated.blowing_engine.heat").append(easyFloat(lastHeatReading)).withStyle(ChatFormatting.RED)));
        if (currentTemp != 0f){
            tempAndCoolInfo(tooltip);
        }
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

        return true;
    }
}
