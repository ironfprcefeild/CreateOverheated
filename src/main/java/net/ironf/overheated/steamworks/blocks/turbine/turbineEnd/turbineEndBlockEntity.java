package net.ironf.overheated.steamworks.blocks.turbine.turbineEnd;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.pressureChamber.core.ChamberCoreBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineFan.turbineFanBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.machines.CapableMachineBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
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
        tank = createInventory();
        capability = tank;

    }

    //Fluids
    IFluidHandler capability;
    SmartFluidTank tank;
    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(8000, this::onFluidStackChanged) {};
    }

    private void onFluidStackChanged(FluidStack fluidStack) {
        setChanged();
        sendData();
    }
    public static void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityEntry<? extends turbineEndBlockEntity> me) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                me.get(),
                turbineEndBlockEntity::getTank);

    }
    public SmartFluidTank getTank(Object c){
        return tank;
    }
    public void setFluid(FluidStack stack) {
        this.tank.setFluid(stack);
    }
    public FluidStack getFluidStack() {
        return this.tank.getFluid();
    }


    //Kinetics
    @Override
    public float getGeneratedSpeed() {
        return isFan ? 0 : convertToDirection(Math.min((float) thisSpinsDrain /10,256), getBlockState().getValue(turbineEndBlock.FACING));
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = isFan ? 0 : 4096;
        this.lastCapacityProvided = capacity;
        return capacity;
    }


    @Override
    public float calculateStressApplied() {
        float impact = isFan ? (Math.min((float) thisSpinsDrain/10,256)*16) : 0;
        this.lastStressApplied = impact;
        return impact;
    }


    //Doing Things

    public int thisSpinsDrain = 0;
    boolean turbineTooSmall = false;
    boolean turbineIntakeLow = false;
    boolean turbineIntakePressureLow = false;
    boolean outtakeFull = false;
    boolean noIntake = false;
    boolean tooLong = false;

    boolean combustionEngine = false;
    boolean isFan = false;

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
        combustionEngine = false;
        isFan = false;
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

                    //if any of these are true, the turbine is invalid or has stopped operating, so we set the drain to 0 and break


                   if (drain < 1){
                        //The turbine does not have a high enough drain to operate, too teeny weeny
                       turbineTooSmall = true;
                   } else if (intakeTank.getTankInventory().getFluid().getAmount() < drain){
                        //The intake tank doesn't have enough fluid for the drain
                       turbineIntakeLow = true;
                   } else if(1 > pressureLevel){
                        //The pressure of the fluid in the intake is not high enough to run a turbine
                       turbineIntakePressureLow = true;
                   } else if (capacity - tank.getFluid().getAmount() < drain){
                        //The outtakes tank is full and cannot accept more
                        outtakeFull = true;
                   } else {
                        //Drain the intake tank
                        intakeTank.getTankInventory().drain(drain, IFluidHandler.FluidAction.EXECUTE);
                        //Fill this tank
                        int heatLevel = AllSteamFluids.getSteamHeat(intakeTank.getTankInventory().getFluid());
                        tank.setFluid(AllSteamFluids.getSteamFromValues(pressureLevel - 1, heatLevel,getFluidStack().getAmount() + drain));
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
                } else if (AllBlocks.COMBUSTION_ENGINE.has(check) && level.getBlockEntity(bp.relative(turbineDirection)) instanceof ChamberCoreBlockEntity CoreBE){
                    int drain = i * radius * 20;

                    if (drain < 1){
                        turbineTooSmall = true;
                    } else if (CoreBE.combustionTimer < drain){
                        turbineIntakeLow = true;
                    } else {
                        //We are operating a combustion engine wow wow wow
                        combustionEngine = true;
                        thisSpinsDrain = drain;
                        reActivateSource = true;
                        recentLength = i;
                        recentRadius = radius;
                        CoreBE.drainCombustion(drain);
                        return;
                    }
                } else if (check.is(AllBlocks.TURBINE_FAN.get()) && level.getBlockEntity(bp) instanceof turbineFanBlockEntity FanBE){
                    int drain = i *radius*20;
                    if (drain < 1){
                        turbineTooSmall = true;
                    } else {
                        float speed = getSpeed();
                        FanBE.updateFromTurbine((int) (drain*(speed/256)),radius,Math.signum(speed) == -1,true);

                        isFan = true;
                        thisSpinsDrain = drain;
                        reActivateSource = true;
                        recentLength = i;
                        recentRadius = radius;
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
    protected void read(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.read(tag, r, clientPacket);
        this.thisSpinsDrain = tag.getInt("recent_drain");
        this.recentLength = tag.getInt("recent_length");
        this.recentRadius = tag.getInt("recent_radius");
        tank.readFromNBT(r,tag.getCompound("tank"));
    }
    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.write(tag, r, clientPacket);
        tag.putInt("recent_drain",this.thisSpinsDrain);
        tag.putInt("recent_length",this.recentLength);
        tag.putInt("recent_radius",this.recentRadius);
        tag.put("tank",tank.writeToNBT(r,tag));

    }


    //Goggles

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip,isPlayerSneaking);
        containedFluidTooltip(tooltip,isPlayerSneaking,tank);
        if (isFan){
            if (turbineTooSmall){
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.too_small")));
            } else if (tooLong){
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.too_long")));
            } else {
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.info_header")));
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.length").append(String.valueOf(recentLength)),1));
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.radius").append(String.valueOf(recentRadius)),1));

                tooltip.add(GoggleHelper.addIndent((Component.translatable("coverheated.turbine.fan"))));
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.airflow").withStyle(ChatFormatting.WHITE)));
                tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat((20 * recentLength * recentRadius * (getSpeed()/256)) /256)).withStyle(ChatFormatting.AQUA),1));
                return true;
            }
        } else if (turbineIntakePressureLow){
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
            if (combustionEngine){
                tooltip.add(GoggleHelper.addIndent((Component.translatable("coverheated.turbine.combustion_mode"))));
            }
            if (isPlayerSneaking) {
                int Drain = recentLength * recentRadius * 20;
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.drain.amount").append(String.valueOf(Drain)).append(Component.translatable("coverheated.turbine.drain." + (combustionEngine ? "alt" : "in"))).append(String.valueOf(lazyTickCounter)).append(Component.translatable("coverheated.turbine.drain.ticks")), 1));
                if (!combustionEngine){
                    tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.drain.steam_vent.requires").append(String.valueOf(Drain / 40)).append(Component.translatable("coverheated.turbine.drain.steam_vent.to_run")), 1));
                }
                if (Drain > 2560) {
                    tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.turbine.drain.too_much"), 1));
                }
            } else {
                tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.tooltip.crouch_for_more_info"), 1));
            }

        }
        return true;
    }
}
