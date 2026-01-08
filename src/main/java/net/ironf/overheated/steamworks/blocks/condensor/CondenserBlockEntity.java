package net.ironf.overheated.steamworks.blocks.condensor;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

import static net.ironf.overheated.steamworks.blocks.condensor.CondensingRecipeHandler.condensingHandler;

public class CondenserBlockEntity extends SmartMachineBlockEntity implements IHaveGoggleInformation {

    public CondenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    int timer = 5;
    HeatData generated = HeatData.empty();
    int heatTimer = 75;
    //Each condenser, when operating at perfect efficiency, uses about 1 Steam Vent
    @Override
    public void tick() {
        super.tick();
        if (heatTimer-- == 0){
            heatTimer = 75;
            generated = HeatData.empty();
        }
        if (timer-- == 0){
            timer = 5;

            //Get above tank
            IFluidTank above = getTank(Direction.UP);
            if (above == null) return;
            //Get fluid above the tank, return if the fluid cannot be condensed
            FluidStack input = above.getFluid();
            CondensingOutputBundle bundle = condensingHandler.getOrDefault(input.getFluid(),null);
            //If the recipe exists, and our current temperature is below the maximum
            if (bundle != null && bundle.minTemp >= getCurrentTemp()) {
                //Get the appropriate resulting fluid, get the tank below, compare the info. If tank is null or fluids do not match, return
                FluidStack resultFluid = bundle.output;
                IFluidTank below = getTank(Direction.DOWN);
                if (below == null) return;

                while (bundle.minTemp >= getCurrentTemp()
                        && below.fill(resultFluid, IFluidHandler.FluidAction.SIMULATE) == resultFluid.getAmount()
                        && above.drain(1, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1) {
                    addTemp(bundle.addTemp);
                    below.fill(resultFluid, IFluidHandler.FluidAction.EXECUTE);
                    above.drain(1, IFluidHandler.FluidAction.EXECUTE);

                    heatTimer = 75;
                    generated = bundle.outputHeat;
                }
            }
        }
    }
    public HeatData getGeneratedHeat() {
        return generated;
    }

    @Override
    public boolean hasPassiveCooling() {return true;}
    @Override
    public CoolingData getPassiveCooling() {
        return new CoolingData(4,-10);
    }

    @Override
    public boolean doCooling() {
        return true;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tempAndCoolInfo(tooltip);
        GoggleHelper.heatTooltip(tooltip,generated, HeatDisplayType.SUPPLYING);
        return true;
    }

    public IFluidTank getTank(Direction in){
        BlockPos pos = getBlockPos().relative(in);
        if (level.getBlockState(pos).getBlock() == AllBlocks.PRESSURIZED_CASING.get()) {pos = pos.relative(in);}

        BlockEntity be = level.getBlockEntity(pos);
        FluidTankBlockEntity tank = (be instanceof FluidTankBlockEntity) ? ((FluidTankBlockEntity) be).getControllerBE() : null;
        return (tank != null) ? tank.getTankInventory() : null;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        timer = tag.getInt("timer");
        heatTimer = tag.getInt("heat_timer");
        generated = HeatData.readTag(tag,"generated_heat");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",timer);
        tag.putInt("heat_timer",heatTimer);
        HeatData.writeTag(tag,generated,"generated_heat");
    }



}
