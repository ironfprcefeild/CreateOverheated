package net.ironf.overheated.steamworks.blocks.condensor;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.colants.LaserCoolingHandler;
import net.ironf.overheated.steamworks.blocks.heatsink.HeatSinkHelper;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static net.ironf.overheated.steamworks.blocks.condensor.CondensingRecipeHandler.*;

public class CondenserBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, HeatSinkHelper {

    public CondenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    int timer = 8;

    int coolantMetaTimer = 8;
    float Heat;
    float coolantTemp;
    //Each condenser, when operating at perfect efficiency, uses about 1 Steam Vent
    @Override
    public void tick() {
        super.tick();
        if (timer-- == 0){
            timer = 8;

            //Get above tank
            IFluidTank above = getTank(Direction.UP);
            if (above == null) return;
            //Get fluid above the tank, return if the fluid cannot be condensed
            FluidStack input = above.getFluid();
            CondensingOutputBundle bundle = condensingHandler.getOrDefault(input.getFluid(),null);
            if (bundle != null && bundle.minTemp >= Heat) {
                //Get the appropriate resulting fluid, get the tank below, compare the info. If tank is null or fluids do not match, return
                FluidStack resultFluid = bundle.output;
                IFluidTank below = getTank(Direction.DOWN);
                if (below == null) return;

                if (below.fill(resultFluid, IFluidHandler.FluidAction.SIMULATE) <= 0) return;
                Heat = Heat + bundle.addTemp;
                below.fill(resultFluid, IFluidHandler.FluidAction.EXECUTE);
                above.drain(1, IFluidHandler.FluidAction.EXECUTE);
            }

            coolantTemp = LaserCoolingHandler.heatHandler.containsKey(getFluid()) ? -LaserCoolingHandler.heatHandler.get(getFluid()) : 0;
            Heat = Math.max(coolantTemp,Heat - ((32 + getHeatSunkenFrom(getBlockPos(),level)) / 16));
            if (coolantMetaTimer-- == 0) {
                tank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.EXECUTE);
                coolantMetaTimer = 8;
            }
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        float sunken = getHeatSunkenFrom(getBlockPos(),level);
        if (sunken > 0) {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.thermometer.total_sunken_heat").withStyle(ChatFormatting.WHITE)));
            tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(sunken)).withStyle(ChatFormatting.AQUA), 1));
        }
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.condenser.heat").withStyle(ChatFormatting.WHITE)));
        tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(Heat)).withStyle(ChatFormatting.AQUA), 1));
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.condenser.coolant").append(Component.literal(GoggleHelper.easyFloat(coolantTemp)).withStyle(ChatFormatting.AQUA))));

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
        coolantTemp = tag.getFloat("coolant_temp");
        Heat = tag.getFloat("gained_heat");
        coolantMetaTimer = tag.getInt("coolant_timer");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",timer);
        tag.putFloat("coolant_temp",coolantTemp);
        tag.putFloat("gained_heat",Heat);
        tag.putInt("coolant_timer",coolantMetaTimer);
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

    public Fluid getFluid() {
        return this.tank.getPrimaryHandler().getFluid().getFluid();
    }

    public FluidStack getFluidStack() {
        return this.tank.getPrimaryHandler().getFluid();
    }
}
