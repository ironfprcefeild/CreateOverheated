package net.ironf.overheated.batteries.charger;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import net.ironf.overheated.batteries.AllBatteryItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ChargerBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {
    public ChargerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    ///Kinetics
    @Override
    public float calculateStressApplied() {
        float impact = 64f;
        this.lastStressApplied = impact;
        return impact;
    }

    /// Handlers
    private LazyOptional<IItemHandler> LazyItemHandler = LazyOptional.empty();

    private final ItemStackHandler BusyItemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public IItemHandler getInputItems() {
        return BusyItemHandler;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        LazyItemHandler = LazyOptional.of(() -> BusyItemHandler);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        LazyItemHandler.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }


    ///Doing Stuff

    //When progress >= 15360 (or 256*60), the charging is complete.
    float progress = 0;
    int tickTimer = 20;

    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- <= 0 && !level.isClientSide){
            tickTimer = 20;
            System.out.println(progress);
            progress += Math.abs(getSpeed());
            if (progress >= 15360){
                progress = performCharge() ? 0 : 15104;
            }
        }
    }

    public boolean performCharge() {

        BlockEntity BE = level.getBlockEntity(getBlockPos().above());
        if (BE != null && BE.getType() == AllBlockEntityTypes.DEPOT.get()) {
            DepotBlockEntity DPBE = ((DepotBlockEntity) BE);
            ItemStack depotItem = DPBE.getHeldItem();
            if (!depotItem.isEmpty()){
                return false;
            }

            LazyOptional<IItemHandler> DepotHandler = DPBE.getCapability(ForgeCapabilities.ITEM_HANDLER,Direction.UP);


            //Look For Heating
            BlockPos below = getBlockPos().below();
            float heat = BoilerHeaters.getActiveHeat(level, below, level.getBlockState(below));
            //Overheated.LOGGER.info("Trying to transform or charge. Heat: " + heat);
            //Overheated.LOGGER.info("Item Handler, " + BusyItemHandler.getStackInSlot(0).getDescriptionId() +" " + BusyItemHandler.getStackInSlot(0).getCount());

            if (heat >= 1 && BusyItemHandler.getStackInSlot(0).is(AllBatteryItems.getBatteryItem((int)heat)) && BusyItemHandler.getStackInSlot(0).getCount() >= 4) {
                //Transform
                //Overheated.LOGGER.info("Transforming");
                DepotHandler.ifPresent(ih ->
                        ih.insertItem(0,AllBatteryItems.getBattery((int)heat+1).asStack(1),false));
                BusyItemHandler.extractItem(0,4,false);
                return true;
            } else if (heat == -1 && BusyItemHandler.extractItem(0,1,true) == AllBatteryItems.getBattery(0).asStack(1)) {
                //Charge Empty
                //Overheated.LOGGER.info("Charging Empty");
                DepotHandler.ifPresent(ih ->
                        ih.insertItem(0, AllBatteryItems.getBattery(1).asStack(1), false));
                BusyItemHandler.extractItem(0, 1, false);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("tick_timer");
        progress = tag.getFloat("charge_progress");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("tick_timer",tickTimer);
        tag.putFloat("charge_progress",progress);
    }
}
