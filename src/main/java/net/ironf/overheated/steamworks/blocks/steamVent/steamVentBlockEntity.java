package net.ironf.overheated.steamworks.blocks.steamVent;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.machines.CapableMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.ref.WeakReference;
import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class steamVentBlockEntity extends CapableMachineBlockEntity implements IHaveGoggleInformation {
    public steamVentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        source = new WeakReference<>(null);
    }



    //Getting Boiler Tank (stolen from steam engine code)
    public WeakReference<FluidTankBlockEntity> source;
    public FluidTankBlockEntity getOtherTank() {
        FluidTankBlockEntity tank = source.get();
        if (tank == null || tank.isRemoved()) {
            if (tank != null)
                source = new WeakReference<>(null);
            Direction facing = steamVentBlock.getAttachedDirection(getBlockState());
            BlockEntity be = level.getBlockEntity(worldPosition.relative(facing));
            if (be instanceof FluidTankBlockEntity tankBe)
                source = new WeakReference<>(tank = tankBe);
        }
        if (tank == null)
            return null;
        return tank.getControllerBE();
    }


    ///Doing Things

    float processingTicks = 75;
    boolean isSlow = false;
    @Override
    public void tick() {
        super.tick();
        FluidTankBlockEntity tank = getOtherTank();
        if (tank != null) {
            BoilerData boiler = tank.boiler;
            int tier = getActualHeat(boiler,tank);
            if (tier > 0 && processingTicks-- < 1) {
                setFluid(
                        AllSteamFluids.getSteamFromValues(
                                ((int) (Math.floor((double) (tier - 1) / 6) + 1)),
                                0,
                                getFluidStack().getAmount() + 10));
                processingTicks = 75 + Math.max(0,15 * (boiler.attachedEngines - tier));
                isSlow = processingTicks != 75;
            }
        }

    }

    public int getActualHeat(BoilerData boiler, FluidTankBlockEntity controller) {
        int forBoilerSize = boiler.getMaxHeatLevelForBoilerSize(controller.getTotalTankSize());
        int forWaterSupply = boiler.getMaxHeatLevelForWaterSupply();
        return Math.min(boiler.activeHeat, Math.min(forWaterSupply, forBoilerSize));
    }

    public int getActualHeat(FluidTankBlockEntity controller) {
        return getActualHeat(controller.boiler,controller);
    }


    public int getFluidCapacity() {
        return 1000;
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.read(tag, r, clientPacket);
        this.processingTicks = tag.getFloat("processing_ticks");
        this.isSlow = tag.getBoolean("is_slow");

    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider r,  boolean clientPacket) {
        super.write(tag, r, clientPacket);
        tag.putFloat("processing_ticks",this.processingTicks);
        tag.putBoolean("is_slow",this.isSlow);

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip,isPlayerSneaking);
        if (isSlow) {
            tooltip.add(addIndent(Component.translatable("coverheated.steam_vent.slow")));
        }
        return true;
    }


}
