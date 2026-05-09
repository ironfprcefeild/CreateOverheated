package net.ironf.overheated.utility.machines;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
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

import java.util.ArrayList;
import java.util.List;

public abstract class CapableMachineBlockEntity extends MachineBlockEntity implements IHaveGoggleInformation {
    public CapableMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        if (getFluidCapacity() > 0) {
            for (int i = 0; i < getInventoryCount(); i++) {
                tanks.set(i,createInventory());
                fluidCapabilities.set(i,tanks.get(i));
            }
        }

        if (getItemStackCapacity() > 0){
            for (int i = 0; i < getInventoryCount(); i++) {
                inventories.set(i, new SmartInventory(1, this)
                        .whenContentsChanged(k -> this.onItemContentsChanged()));
            }
        }

    }
    /// Capabilities
    public static void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityEntry<? extends CapableMachineBlockEntity> me, int TankCount, int InvCount) {
        if (TankCount > 0) {
            for (int i = 0; i < TankCount; i++) {
                int finalI = i;
                event.registerBlockEntity(
                        Capabilities.FluidHandler.BLOCK,
                        me.get(),
                        (be, context) -> be.getTank(finalI));
            }
        }
        if (InvCount > 0){
            for (int i = 0; i < InvCount; i++) {
                int finalI = i;
                event.registerBlockEntity(
                        Capabilities.ItemHandler.BLOCK,
                        me.get(),
                        (be, context) -> be.getInventory(finalI));
            }
        }

    }


    /// Fluids
    public int getFluidCapacity(){
        return 0;
    }
    public int getFluidTankCount(){
        return getFluidCapacity() > 0 ? 1 : 0;
    }

    protected ArrayList<IFluidHandler> fluidCapabilities;
    public ArrayList<SmartFluidTank> tanks;

    public SmartFluidTank getTank(int i){
        return tanks.get(i);
    }
    public SmartFluidTank Tank(){
        return tanks.get(0);
    }
    public SmartFluidTank inputTank(){
        return getTank(0);
    }
    public SmartFluidTank outputTank(){
        return getTank(1);
    }
    protected SmartFluidTank createInventory() {
        return new SmartFluidTank(getFluidCapacity(), this::onFluidStackChanged) {};
    }
    protected void onFluidStackChanged(FluidStack newFluidStack) {
        sendData();
        setChanged();
    }

    public void setFluid(FluidStack stack) {
        getTank(0).setFluid(stack);
    }
    public FluidStack getFluidStack() {
        return getTank(0).getFluid();
    }
    public void setFluid(FluidStack stack, int tank) {
        getTank(tank).setFluid(stack);
    }
    public FluidStack getFluidStack(int tank) {
        return getTank(tank).getFluid();
    }

    /// Items
    public int getItemStackCapacity(){
        return 0;
    }
    public int getInventoryCount(){
        return getItemStackCapacity() > 0 ? 1 : 0;
    }

    public ArrayList<SmartInventory> inventories;

    private void onItemContentsChanged() {
        sendData();
        setChanged();
    }

    public SmartInventory getInventory(int i ){
        return inventories.get(i);
    }
    public SmartInventory inputInventory(){
        return getInventory(0);
    }
    public SmartInventory outputInventory(){
        return getInventory(1);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (getItemStackCapacity() > 0) {
            for (SmartInventory inventory : inventories) {
                ItemHelper.dropContents(level, worldPosition, inventory);
            }
        }
    }

    /// NBT
    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (getFluidCapacity() < 0) {
            for (int i = 0; i < getFluidTankCount(); i++) {
                tanks.get(i).readFromNBT(registries, tag.getCompound(i+"_TankContent"));
            }
        }
        if (getItemStackCapacity() < 0){
            for (int i = 0; i < getInventoryCount(); i++) {
                inventories.get(i).deserializeNBT(registries, tag.getCompound(i+"_Inventory"));
            }
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (getFluidCapacity() < 0) {
            for (int i = 0; i < getFluidTankCount(); i++) {
                tag.put(i+"_TankContent", getTank(i).writeToNBT(registries, new CompoundTag()));
            }
        }
        if (getItemStackCapacity() > 0){
            for (int i = 0; i < getInventoryCount(); i++) {
                tag.put(i+"_Inventory", getInventory(i).serializeNBT(registries));
            }
        }
    }

    /// Goggles
    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (getFluidCapacity() != 0) {
            for (int i = 0; i < getFluidTankCount(); i++) {
                containedFluidTooltip(tooltip, isPlayerSneaking, fluidCapabilities.get(i));
            }
        }
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
