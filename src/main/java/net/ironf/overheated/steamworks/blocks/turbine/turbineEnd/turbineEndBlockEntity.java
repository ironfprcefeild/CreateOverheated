package net.ironf.overheated.steamworks.blocks.turbine.turbineEnd;

import com.mojang.datafixers.TypeRewriteRule;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.steamworks.blocks.turbine.turbineCenter.turbineCenterBlockEntity;
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

public class turbineEndBlockEntity extends GeneratingKineticBlockEntity {
    public turbineEndBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //Kinetics
    @Override
    public float getGeneratedSpeed() {
        return thisSpinsDrain == 0 ? 0 : thisSpinsDrain + 40;
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = (float) thisSpinsDrain;
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    //Doing Things

    public int timeTillNextPacket = 200;
    public int timeLeftSpinning = 0;
    public int thisSpinsDrain = 0;
    @Override
    public void tick() {
        super.tick();
        //If 200 ticks has passed, then check if we have steam in the tank, then run the packet.
        if (timeTillNextPacket == 0) {
            int steamPressure = AllSteamFluids.getSteamPressure(getFluidStack());
            if (steamPressure > 0) {
                BlockEntity Facing = getFacedBE(getMyState());
                //We are facing a turbine, so we should send down a packet
                if (Facing != null && Facing.getType() == AllBlockEntities.TURBINE_CENTER.get()) {
                    ((turbineCenterBlockEntity) Facing).progressSteamPacket(getBlockPos(),steamPressure, 0, 0);
                }
                timeTillNextPacket = 200;
            } else {
                timeTillNextPacket--;
            }
        }
        if (timeLeftSpinning != 0){
            timeLeftSpinning--;
            if (timeLeftSpinning == 0){
                thisSpinsDrain = 0;
            }
        }
    }

    public void handleEndOfPacket(BlockPos startPoint,int pressureLevel, int radius, int turbineNumber){
        //Find turbine size
        int turbineSize = radius * turbineNumber;

        //Drain origin block
        BlockEntity origin = level.getBlockEntity(startPoint);
        if (origin != null && origin.getType() == AllBlockEntities.TURBINE_END.get()){
            FluidStack drainedFluid = ((turbineEndBlockEntity) origin).tank.getPrimaryHandler().drain(turbineSize * 2, IFluidHandler.FluidAction.EXECUTE);
            boolean shouldRotate = (drainedFluid.getAmount() >= turbineSize * 2);

            //prepare to generate SU
            if (shouldRotate) {
                timeLeftSpinning = 205;
                thisSpinsDrain = drainedFluid.getAmount();

                //Fill with steam of lower pressure level
                int fillTankToo = Math.min(8000,drainedFluid.getAmount() + tank.getPrimaryHandler().getFluid().getAmount());
                tank.getPrimaryHandler().setFluid(new FluidStack(AllSteamFluids.getSteamFromValues(pressureLevel,0),fillTankToo));

            } else {
                //refund the steam we took from the origin
                ((turbineEndBlockEntity) origin).setFluid(new FluidStack(AllSteamFluids.getSteamFromValues(pressureLevel,0),drainedFluid.getAmount()));
            }

        }

    }

    public BlockEntity getFacedBE(BlockState myState){
        return (level.getBlockEntity(getBlockPos().relative(myState.getValue(BlockStateProperties.HORIZONTAL_FACING))));
    }

    public BlockState getMyState(){
        return level.getBlockState(getBlockPos());
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tag.putInt("processing_ticks",this.timeTillNextPacket);
        tag.putInt("duration",this.timeLeftSpinning);
        tag.putInt("recent_drain",this.thisSpinsDrain);
    }
    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        this.timeTillNextPacket = tag.getInt("processing_ticks");
        this.timeLeftSpinning = tag.getInt("duration");
        this.thisSpinsDrain = tag.getInt("recent_drain");

    }

    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 8000));
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
}
