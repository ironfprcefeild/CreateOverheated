package net.ironf.overheated.steamworks.blocks.turbine.turbineEnd;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
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
import java.util.List;

public class turbineEndBlockEntity extends GeneratingKineticBlockEntity implements IHaveGoggleInformation {
    public turbineEndBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //Kinetics
    @Override
    public float getGeneratedSpeed() {
        return convertToDirection(thisSpinsDrain != 0 ? thisSpinsDrain + 40 : 0, getBlockState().getValue(turbineEndBlock.FACING));
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = (float) thisSpinsDrain * 128;
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    //todo fix the constantly updating issue

    //Doing Things

    public int processingTicks = 300;
    public int thisSpinsDrain = 0;
    public String displayData = "";



    @Override
    public void tick() {
        if (!level.isClientSide && !reActivateSource) {
            processingTicks--;
            Overheated.LOGGER.info(String.valueOf(processingTicks));
            if (processingTicks < 1) {
                processingTicks = 300;
                checkTurbine();
            }
        }
        super.tick();
    }

    public void checkTurbine(){
        Overheated.LOGGER.info("Mr dipshit banana time");

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
                if (com.simibubi.create.AllBlocks.FLUID_TANK.has(check)){
                    ////Update Turbine Generation, we met a fluid tank so it's a complete turbine
                    FluidTankBlockEntity intakeTank = ((FluidTankBlockEntity) level.getBlockEntity(bp)).getControllerBE();
                    int pressureLevel = AllSteamFluids.getSteamPressure(intakeTank.getTankInventory().getFluid());
                    int turbineSize = i * radius;
                    int drain = turbineSize * 2;
                    Overheated.LOGGER.info("Pressure: " + pressureLevel);
                    Overheated.LOGGER.info("Size: " + turbineSize);
                    Overheated.LOGGER.info("Drain: " + drain);

                    //if any of theese are true, the turbine is invalid or has stopped operating, so we set the drain to 0 and break
                    if (
                            //The intake tank doesn't have enough fluid for the drain
                            intakeTank.getTankInventory().getFluid().getAmount() < drain
                            //The pressure of the steam in the tank is too low (so it's not steam or distilled water)
                            || 1 > pressureLevel
                            //This block doesn't have enough space to store the steam that will be added to it
                            || capacity - tank.getPrimaryHandler().getFluid().getAmount() < drain){
                        Overheated.LOGGER.info("Drain set to 0");
                        thisSpinsDrain = 0;
                        //reActivateSource = true;
                        return;
                    }

                    //Drain the intake tank
                    intakeTank.getTankInventory().drain(drain, IFluidHandler.FluidAction.EXECUTE);
                    //Fill this tank
                    tank.getPrimaryHandler().setFluid(new FluidStack(AllSteamFluids.getSteamFromValues(pressureLevel - 1,0),getFluidStack().getAmount() + drain));
                    //Update Drain value
                    thisSpinsDrain = drain;
                    //Indicate to reactivate
                    reActivateSource = true;

                    //Break out of loop, no need to check further blocks
                    return;
                }
                //If its any block besides a turbine, even if we ended early or reached a fluid tank then we stop the search
                thisSpinsDrain = 0;
                reActivateSource = true;
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
            if (AllBlocks.TURBINE_EXTENSION.has(level.getBlockState(bp))) {
                radiusRating += 0.25;
                for (Direction d2 : Iterate.directions) {
                    if (AllBlocks.TURBINE_EXTENSION.has(level.getBlockState(bp.relative(d).relative(d2)))) {
                        radiusRating += 0.25;
                    }
                }
            }
        }
        return radiusRating;
    }


    public void initialize() {
        super.initialize();
        this.sendData();
        if (!this.hasSource() || this.getGeneratedSpeed() > this.getTheoreticalSpeed()) {
            this.updateGeneratedRotation();
        }
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tag.putInt("processing_ticks",this.processingTicks);
        tag.putInt("recent_drain",this.thisSpinsDrain);
    }
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        this.processingTicks = tag.getInt("processing_ticks");
        this.thisSpinsDrain = tag.getInt("recent_drain");

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
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        tooltip.add(Component.literal(displayData));
        return true;
    }
}
