package net.ironf.overheated.steamworks.blocks.turbine.turbineEnd;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.GoggleHelper;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class turbineEndBlockEntity extends GeneratingKineticBlockEntity implements IHaveGoggleInformation {
    public turbineEndBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(300);
    }

    //Kinetics
    @Override
    public float getGeneratedSpeed() {
        return convertToDirection(Math.min(256,thisSpinsDrain / 10), getBlockState().getValue(turbineEndBlock.FACING));
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = 4096;
        this.lastCapacityProvided = capacity;
        return capacity;
    }


    //Doing Things

    public int thisSpinsDrain = 0;
    boolean turbineTooSmall = false;
    boolean turbineIntakeLow = false;
    boolean turbineIntakePressureLow = false;
    boolean outtakeFull = false;
    boolean noIntake = false;
    boolean tooLong = false;
    int recentLength;
    int recentRadius;

    @Override
    public void lazyTick() {
        super.lazyTick();
        checkTurbine();
    }



    public void checkTurbine(){
        turbineTooSmall = false;
        turbineIntakeLow = false;
        turbineIntakePressureLow = false;
        outtakeFull = false;
        noIntake = false;
        tooLong = false;
        BlockPos origin = getBlockPos();
        int radius = 9999;
        Direction turbineDirection = getBlockState().getValue(BlockStateProperties.FACING).getOpposite();

        ArrayList<Direction> extensionDirections = new ArrayList<>(List.of(Iterate.directions));
        extensionDirections.remove(turbineDirection);
        extensionDirections.remove(turbineDirection.getOpposite());


        //Go back through turbine, the 12 limits the length of a turbine to 12
        int i = 0;
        while (i < 12){
            i++;
            BlockPos bp = origin.relative(turbineDirection,i);
            BlockState check = level.getBlockState(bp);
            //If its not a turbine center, check to see if it's a fluid tank, otherwise break and set drain to 0
            if (!AllBlocks.TURBINE_CENTER.has(check)){
                if (com.simibubi.create.AllBlocks.FLUID_TANK.has(check)) {
                    ////Update Turbine Generation, we met a fluid tank so it's a complete turbine
                    FluidTankBlockEntity intakeTank = ((FluidTankBlockEntity) level.getBlockEntity(bp)).getControllerBE();
                    int pressureLevel = AllSteamFluids.getSteamPressure(intakeTank.getTankInventory().getFluid());
                    int drain = i * radius * 20;

                    //if any of theese are true, the turbine is invalid or has stopped operating, so we set the drain to 0 and break


                   if (drain < 1){
                        //The turbine does not have a high enough drain to operate, too teeny weeny
                       turbineTooSmall = true;
                   } else if (intakeTank.getTankInventory().getFluid().getAmount() < drain){
                        //The intake tank doesn't have enough fluid for the drain
                       turbineIntakeLow = true;
                   } else if(1 > pressureLevel){
                        //The pressure of the fluid in the intake is not high enough to run a turbine
                       turbineIntakePressureLow = true;
                   } else if (capacity - tank.getPrimaryHandler().getFluid().getAmount() < drain){
                        //The outtakes tank is full and cannot accept more
                        outtakeFull = true;
                   } else {
                        //Drain the intake tank
                        intakeTank.getTankInventory().drain(drain, IFluidHandler.FluidAction.EXECUTE);
                        //Fill this tank
                        tank.getPrimaryHandler().setFluid(AllSteamFluids.getSteamFromValues(pressureLevel - 1, 0,getFluidStack().getAmount() + drain));
                        //Update Drain value
                        thisSpinsDrain = drain;
                        //Indicate to reactivate
                        reActivateSource = true;
                        //update display
                        recentLength = i;
                        recentRadius = radius;
                        //Break out of loop, no need to check further blocks
                        return;
                   }
                }
                //If its any block besides a turbine, even if we ended early or reached a fluid tank then we stop the search
                //This code is also reached when any of the big conditions are true
                thisSpinsDrain = 0;
                reActivateSource = true;
                noIntake = true;
                return;
            } else {
                //We are at a center point, so just update radius
                radius = Math.min(radius,getRadiusOfCenterAt(bp,extensionDirections));
            }
        }
        tooLong = true;
    }

    public int getRadiusOfCenterAt(BlockPos checkAt, ArrayList<Direction> directions) {
        int radiusRating = 0;
        BlockPos bp;
        Set<BlockPos> secondPass = new LinkedHashSet<>();
        //Find blockpos of cardinally adjacent blocks
        for (Direction d : directions) {
            bp = checkAt.relative(d);
            if (AllBlocks.TURBINE_EXTENSION.get() == level.getBlockState(bp).getBlock()) {
                radiusRating += 1;
                for (Direction d2 : directions){
                    secondPass.add(bp.relative(d2));
                }
            }
        }
        for (BlockPos bp2 : secondPass){
            if (AllBlocks.TURBINE_EXTENSION.get() == level.getBlockState(bp2).getBlock()) {
                radiusRating +=1;
            }
        }
        return Math.max(radiusRating,1);
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
        this.recentLength = tag.getInt("recent_length");
        this.recentRadius = tag.getInt("recent_radius");

    }
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("recent_drain",this.thisSpinsDrain);
        tag.putInt("recent_length",this.recentLength);
        tag.putInt("recent_radius",this.recentRadius);

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
        if (turbineIntakePressureLow){
            tooltip.add(GoggleHelper.addIndent((Component.translatable("coverheated.turbine.intake.low_pressure"))));
        } else if (turbineIntakeLow){
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.intake.low")));
        } else if(turbineTooSmall){
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.too_small")));
        } else if (outtakeFull){
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.outtake_full")));
        } else if (noIntake) {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.no_intake")));
        } else if (tooLong){
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.too_long")));
        } else {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.info_header")));
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.length").append(String.valueOf(recentLength)),1));
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.radius").append(String.valueOf(recentRadius)),1));
            if (isPlayerSneaking) {
                int Drain = recentLength * 20 * recentRadius;
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.drain.amount").append(String.valueOf(Drain)).append(Component.translatable("coverheated.turbine.drain.in")).append(String.valueOf(lazyTickCounter)).append(Component.translatable("coverheated.turbine.drain.ticks")),1));
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.drain.steam_vent.requires").append(String.valueOf(Drain / 40)).append(Component.translatable("coverheated.turbine.drain.steam_vent.to_run")),1));
                if (Drain > 2560){
                    tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.drain.too_much"),1));
                }
            } else {
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.tooltip.crouch_for_more_info"),1));
            }
        }
        return true;
    }
}
