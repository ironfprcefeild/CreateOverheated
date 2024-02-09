package net.ironf.overheated.steamworks.blocks.turbine.turbineEnd;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.laserOptics.backend.ILaserAbsorber;
import net.ironf.overheated.steamworks.steamFluids.AllSteamFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.ironf.overheated.Overheated.lang;

public class turbineEndBlockEntity extends GeneratingKineticBlockEntity implements IHaveGoggleInformation, ILaserAbsorber {
    public turbineEndBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(300);
    }

    //Kinetics
    @Override
    public float getGeneratedSpeed() {
        return convertToDirection(thisSpinsDrain != 0 ? thisSpinsDrain + 16 : 0, getBlockState().getValue(turbineEndBlock.FACING));
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = (float) thisSpinsDrain * 16;
        this.lastCapacityProvided = capacity;
        return capacity;
    }


    //Doing Things

    public int thisSpinsDrain = 0;
    public List<Component> displayData = new ArrayList<>();





    @Override
    public void lazyTick() {
        super.lazyTick();
        checkTurbine();
    }



    public void checkTurbine(){

        BlockPos origin = getBlockPos();
        int radius = 999;
        Direction turbineDirection = getBlockState().getValue(BlockStateProperties.FACING).getOpposite();
        //Go back through turbine, the 13 limits the length of a turbine to 12
        int i = 0;
        while (i < 13){
            i++;
            BlockPos bp = origin.relative(turbineDirection,i);
            BlockState check = level.getBlockState(bp);
            //If its not a turbine center, check to see if it's a fluid tank, otherwise break and set drain to 0
            if (!AllBlocks.TURBINE_CENTER.has(check)){
                if (com.simibubi.create.AllBlocks.FLUID_TANK.has(check)) {
                    ////Update Turbine Generation, we met a fluid tank so it's a complete turbine
                    FluidTankBlockEntity intakeTank = ((FluidTankBlockEntity) level.getBlockEntity(bp)).getControllerBE();
                    int pressureLevel = AllSteamFluids.getSteamPressure(intakeTank.getTankInventory().getFluid());
                    int drain = i * radius * 2;
                    //if any of theese are true, the turbine is invalid or has stopped operating, so we set the drain to 0 and break


                   if (drain < 1){
                        //The turbine does not have a high enough drain to operate, too teeny weeny
                       setInvalidDisplayInfo("turbine.too_small");
                    } else if (intakeTank.getTankInventory().getFluid().getAmount() < drain){
                        //The intake tank doesn't have enough fluid for the drain
                       setInvalidDisplayInfo("turbine.intake.low");
                    } else if(1 > pressureLevel){
                        //The pressure of the fluid in the intake is not high enough to run a turbine
                       setInvalidDisplayInfo("turbine.intake.low_pressure");
                    } else if (capacity - tank.getPrimaryHandler().getFluid().getAmount() < drain){
                        //The outtakes tank is full and cannot accept more
                        setInvalidDisplayInfo("turbine.outtake_full");

                    } else {
                        //Drain the intake tank
                        intakeTank.getTankInventory().drain(drain, IFluidHandler.FluidAction.EXECUTE);
                        //Fill this tank
                        tank.getPrimaryHandler().setFluid(new FluidStack(AllSteamFluids.getSteamFromValues(pressureLevel - 1, 0), getFluidStack().getAmount() + drain));
                        //Update Drain value
                        thisSpinsDrain = drain;
                        //Indicate to reactivate
                        reActivateSource = true;
                        //update display
                        setDisplayInfo(radius,i,drain);
                        //Break out of loop, no need to check further blocks
                        return;
                    }
                }
                //If its any block besides a turbine, even if we ended early or reached a fluid tank then we stop the search
                //This code is also reached when any of the big conditions are true
                thisSpinsDrain = 0;
                reActivateSource = true;
                setInvalidDisplayInfo("turbine.no_intake");
                return;
            } else {
                //We are at an extension so just update radius
                radius = Math.min(radius,getRadiusOfCenterAt(bp));
            }
        }
    }

    public int getRadiusOfCenterAt(BlockPos checkAt) {
        int radiusRating = 1;

        //Find blockpos of cardinally adjacent blocks
        for (Direction d : Iterate.directions) {
            BlockPos bp = checkAt.relative(d);
            if (AllBlocks.TURBINE_EXTENSION.get() == level.getBlockState(bp).getBlock()) {
                radiusRating += 1;
                for (Direction d2 : Iterate.directions) {
                    if (AllBlocks.TURBINE_EXTENSION.get() == (level.getBlockState(bp.relative(d).relative(d2)).getBlock())) {
                        radiusRating += 1;
                    }
                }
            }
        }
        return radiusRating;
    }

    //For operating status
    public void setDisplayInfo(int radius, int length, int drain){
        displayData.clear();
        displayData.add(lang.translate("turbine.radius").text(String.valueOf(radius)).component());
        displayData.add(lang.translate("turbine.length").text(String.valueOf(length)).component());
        displayData.add(lang.translate("turbine.drain.amount").text(String.valueOf(drain)).component());
        double drainPerTick = (Math.ceil((double) drain / 300));
        if (drainPerTick > 1) {
            displayData.add(lang.translate("turbine.drain.rate").space().text(String.valueOf(drainPerTick)).space().translate("turbine.drain.every_tick").component());
        }
    }

    public void setInvalidDisplayInfo(String key){
        displayData.clear();
        displayData.add(lang.translate("turbine.error").text(System.lineSeparator()).translate(key).component());
    }



    public void initialize() {
        super.initialize();
        this.sendData();
        if (!this.hasSource() || this.getGeneratedSpeed() > this.getTheoreticalSpeed()) {
            this.updateGeneratedRotation();
        }
        setLazyTickRate(300);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.thisSpinsDrain = tag.getInt("recent_drain");
    }
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("recent_drain",this.thisSpinsDrain);

    }

    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    public static int capacity = 8000;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, capacity));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.tank.getPrimaryHandler());
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyFluidHandler.invalidate();
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }
    public void setFluid(FluidStack stack) {
        this.tank.getPrimaryHandler().setFluid(stack);
    }
    public FluidStack getFluidStack() {
        return this.tank.getPrimaryHandler().getFluid();
    }

    //Goggles

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip,isPlayerSneaking);
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        tooltip.addAll(displayData);
        return true;
    }
}
