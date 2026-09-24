package net.ironf.overheated.metalWorking.bellow;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class BellowBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
    public BellowBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tank = createInventory();
        capability = tank;
    }
    //Fluids
    IFluidHandler capability;
    SmartFluidTank tank;
    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(2000, this::onFluidStackChanged) {};
    }

    private void onFluidStackChanged(FluidStack fluidStack) {
        setChanged();
        sendData();
    }
    public static void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityEntry<? extends BellowBlockEntity> me) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                me.get(),
                BellowBlockEntity::getTank);

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


    //Doing Stuff
    public static float bellowTicks = 75*128;
    float processingTicks = bellowTicks;

    @Override
    public void tick() {
        super.tick();
        processingTicks -= Math.abs(getSpeed());
        if (processingTicks < 1){
            processingTicks = bellowTicks;
            tank.fill(new FluidStack(AllSteamFluids.AIR.SOURCE.get().getSource(),10), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    //Goggles
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip,isPlayerSneaking,tank);
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    //Rotating
    @Override
    public float calculateStressApplied() {
        float impact = 4f;
        this.lastStressApplied = impact;
        return impact;
    }

    //Read/Write
    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.read(tag, r, clientPacket);
        processingTicks = tag.getFloat("timer");
        tank.readFromNBT(r,tag.getCompound("tank"));
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.write(tag, r, clientPacket);
        tag.putFloat("timer",processingTicks);
        tag.put("tank",tank.writeToNBT(r,tag));
    }
}
