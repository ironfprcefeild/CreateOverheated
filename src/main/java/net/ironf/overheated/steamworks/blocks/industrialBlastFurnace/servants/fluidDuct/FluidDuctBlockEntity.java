package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.fluidDuct;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.logistics.chute.SmartChuteFilterSlotPositioning;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.block.BlastFurnaceControllerBlockEntity;
import net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.servants.BlastFurnaceServantBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static net.ironf.overheated.steamworks.AllSteamFluids.isSteam;

public class FluidDuctBlockEntity extends BlastFurnaceServantBlockEntity {
    public FluidDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //Filter Slot
    FilteringBehaviour filtering;
    public boolean isExtracting(){
        return !filtering.getFilter().isEmpty();
    }

    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(filtering =
                new FilteringBehaviour(this, new SmartChuteFilterSlotPositioning()));
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 1000));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyFluidHandler = LazyOptional.of(() -> tank.getPrimaryHandler());
    }

    @Override
    public void invalidate() {
        super.invalidate();
        lazyFluidHandler.invalidate();
    }



    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.FLUID_HANDLER && controllerPos != null) {
            if (level.getBlockEntity(controllerPos) instanceof BlastFurnaceControllerBlockEntity ibf) {
                return ibf.MainTank.getCapability().cast();
            }
        }
        /*
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }

         */
        return super.getCapability(cap, side);
    }

    public int tickTimer = 20;


    @Override
    public void tick() {
        super.tick();
        /*
        if (tickTimer-- <= 0) {
            tickTimer = 20;
            if (controllerPos != null && level.getBlockEntity(controllerPos) instanceof BlastFurnaceControllerBlockEntity IBF) {
                if (isExtracting()) {
                    //Extracting a fluid
                    for (FluidStack fs : IBF.MainTank.fluids) {
                        if (filtering.forFluids().test(fs)) {
                            fs.shrink(tank.getPrimaryHandler().fill(fs, IFluidHandler.FluidAction.EXECUTE));
                        }
                        return;
                    }
                    if (filtering.forFluids().test(IBF.SteamTank.getPrimaryHandler().getFluid())){
                        //Extracting Steam!
                        SmartFluidTank steamTank = IBF.SteamTank.getPrimaryHandler();
                        steamTank.drain(tank.getPrimaryHandler().fill(steamTank.getFluid(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    } else if (filtering.forFluids().test(IBF.OxygenTank.getPrimaryHandler().getFluid())){
                        //Extracting Oxygen!
                        SmartFluidTank OxygenTank = IBF.OxygenTank.getPrimaryHandler();
                        OxygenTank.drain(tank.getPrimaryHandler().fill(OxygenTank.getFluid(), IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    }
                } else {
                    //Inserting a fluid
                    int insertedAmount;
                    FluidStack fluidStack = tank.getPrimaryHandler().getFluid();
                    if (fluidStack.isEmpty()) {return;}
                    if (isSteam(fluidStack) && (fluidStack.getFluid().isSame(IBF.SteamTank.getPrimaryHandler().getFluid().getFluid()) || IBF.SteamTank.getPrimaryHandler().getFluid().isEmpty())) {
                        //Inserting a Steam
                        insertedAmount = IBF.SteamTank.getPrimaryHandler().fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    } else if (fluidStack.getFluid().getFluidType() == AllGasses.oxygen.FLUID_TYPE.get()) {
                        //Inserting Oxygen
                        insertedAmount = IBF.OxygenTank.getPrimaryHandler().fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    } else {
                        //Inserting any old fluid
                        insertedAmount = IBF.MainTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    }
                    tank.getPrimaryHandler().drain(insertedAmount, IFluidHandler.FluidAction.EXECUTE);
                }

            }
        }

         */
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);

        return true;
    }
}
