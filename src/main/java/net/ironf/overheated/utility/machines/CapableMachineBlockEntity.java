package net.ironf.overheated.utility.machines;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.ironf.overheated.Overheated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
        Overheated.LOGGER.info("CO: A capable machine has been instantiated");
        if (getFluidCapacity() > 0) {
            Overheated.LOGGER.info("    It has fluids: " + getFluidTankCount());
            tanks = new ArrayList<SmartFluidTank>(getFluidTankCount());
            fluidCapabilities = new ArrayList<IFluidHandler>(getFluidTankCount());
            for (int i = 0; i < getFluidTankCount(); i++) {
                tanks.add(createInventory());
                fluidCapabilities.add(tanks.get(i));
            }
        }

        if (getItemStackCapacity() > 0){
            Overheated.LOGGER.info("    It has items");
            inventories = new ArrayList<SmartInventory>(getInventoryCount());
            for (int i = 0; i < getInventoryCount(); i++) {
                inventories.add(new SmartInventory(1, this)
                        .whenContentsChanged(k -> this.onItemContentsChanged()));
            }
        }

    }


    /// Capabilities
    /// Call this method on the register capabilities event.
    ///     Event - The event item passed
    ///     me - The Block entity entry for which capabilities are to be registered
    ///     fluids - if this has fluids
    ///     items -  if this has items
    public static void registerCapabilities(RegisterCapabilitiesEvent event, BlockEntityEntry<? extends CapableMachineBlockEntity> me, boolean fluids, boolean items) {
        if (fluids) {
            event.registerBlockEntity(
                    Capabilities.FluidHandler.BLOCK,
                    me.get(),
                    CapableMachineBlockEntity::getTank);
        }
        if (items) {
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    me.get(),
                    CapableMachineBlockEntity::getInventory);
        }
    }
    /// Mapping
    //Override these methods for blocks with sided inputs, the returned integer references the tank or inventory in that position of the list
    public int itemOrdinalForSide(Direction side){
        return 0;
    }
    public int tankOrdinalForSide(Direction side){
        return 0;
    }

    /// Fluids
    //How much each tank on this BE holds
    public int getFluidCapacity(){
        return 0;
    }
    //How many tanks are in this BE, each tank holds 1 fluid. Defaults to 1 for BEs with a fluid capacity
    public int getFluidTankCount(){
        return getFluidCapacity() > 0 ? 1 : 0;
    }

    protected ArrayList<IFluidHandler> fluidCapabilities;
    public ArrayList<SmartFluidTank> tanks;

    //Get the tank in the list at position i or direction
    public SmartFluidTank getTank(int i){
        return i == -1 ? null : tanks.get(i);
    }
    public SmartFluidTank getTank(Direction side){
        return getTank(tankOrdinalForSide(side));
    }

    public SmartFluidTank Tank(){
        return tanks.getFirst();
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
        return i == -1 ? null : inventories.get(i);
    }
    public SmartInventory getInventory(Direction d){
        return getInventory(itemOrdinalForSide(d));
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
        return true;
    }

}
