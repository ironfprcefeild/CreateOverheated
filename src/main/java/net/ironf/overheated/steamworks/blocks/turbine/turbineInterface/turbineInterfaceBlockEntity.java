package net.ironf.overheated.steamworks.blocks.turbine.turbineInterface;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.steamworks.blocks.turbine.multiblock.turbineBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineShaft.turbineShaftBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineVent.turbineVentBlock;
import net.ironf.overheated.steamworks.blocks.turbine.turbineVent.turbineVentBlockEntity;
import net.ironf.overheated.steamworks.steamFluids.AllSteamFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import java.util.Optional;

public class turbineInterfaceBlockEntity extends SmartBlockEntity {
    public turbineInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //Doing stuff

    Boolean onVent;
    int processingTicks = 200;

    @Override
    public void tick() {
        super.tick();
        if (processingTicks < 1) {
            if (onVent != null && onVent) {
                runVentTick();
            } else if (onVent != null) {
                runShaftTick();
            }
            processingTicks = 200;
        } else {
            processingTicks--;
        }
    }
    public void setOnVent(Boolean onVent) {
        this.onVent = onVent;
    }

    public void runShaftTick() {
        Optional<turbineShaftBlockEntity> beOp = getShaft();

        //If we on the shaft then get its turbine
        if (beOp.isPresent()){
            turbineBlockEntity turbine = beOp.get().getTurbine();
            //If more than 0 steam (negative can happen), add as much steam as you can do this.
            //If pressure is the same in this and turbine OR if this is empty
            if (
                    turbine.mbSteamOut > 0 &&
                    (turbine.currentPressure == AllSteamFluids.getSteamPressure(tank.getPrimaryHandler().getFluid()) || tank.getPrimaryHandler().getFluid().getAmount() == 0)
            ) {
                int removedAmount = (tank.getPrimaryHandler().getCapacity() - tank.getPrimaryHandler().getFluidAmount());
                turbine.mbSteamOut = turbine.mbSteamOut - removedAmount;

                //Weve purposefully filled up this so just do it now, the fluid is found through the all steam fluids class.
                tank.getPrimaryHandler().fill(new FluidStack(AllSteamFluids.getSteamFromPressure(turbine.currentPressure),removedAmount), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    public void runVentTick() {
        Optional<turbineVentBlockEntity> beOp = getVent();

        //If we on the vent then we push to it, the pushtoturbine method tells us how much steam to drain form this thing
        if (beOp.isPresent()){
            int amountToDrain = beOp.get().pushToTurbine(tank.getPrimaryHandler().getFluid());
            tank.getPrimaryHandler().drain(amountToDrain, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private Optional<turbineVentBlockEntity> getVent() {
        Direction facing = turbineVentBlock.getAttachedDirection(getBlockState());
        BlockEntity be = level.getBlockEntity(worldPosition.relative(facing));
        if (be != null && be.getType() == AllBlockEntities.TURBINE_VENT.get()){
            return Optional.of((turbineVentBlockEntity) be);
        } else {
            return Optional.empty();
        }
    }

    private Optional<turbineShaftBlockEntity> getShaft() {
        Direction facing = turbineVentBlock.getAttachedDirection(getBlockState());
        BlockEntity be = level.getBlockEntity(worldPosition.relative(facing));
        if (be != null && be.getType() == AllBlockEntities.TURBINE_SHAFT.get()){
            return Optional.of((turbineShaftBlockEntity) be);
        } else {
            return Optional.empty();
        }
    }





    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 2000));
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

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        processingTicks = tag.getInt("processing_ticks");
        if (tag.getBoolean("placementUnknown")){
            onVent = null;
        } else {
            onVent = tag.getBoolean("placementStyle");
        }
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (onVent == null) {
            tag.putBoolean("placementUnknown", true);
        } else {
            tag.putBoolean("placementStyle",onVent);
            tag.putBoolean("placementUnknown",false);
        }
        tag.putInt("processing_ticks",processingTicks);
    }
}
