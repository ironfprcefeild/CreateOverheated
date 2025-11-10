package net.ironf.overheated.batteries.charger;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.item.ItemHelper;
import net.ironf.overheated.AllItems;
import net.ironf.overheated.batteries.AllBatteryItems;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.List;

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

    @Override
    public void destroy() {
        super.destroy();
        BlockPos pos = getBlockPos();
        ItemHelper.dropContents(getLevel(), pos, BusyItemHandler);
        if (canTransform) {
            Containers.dropItemStack(getLevel(), pos.getX(), pos.getY(), pos.getZ(), new ItemStack(AllItems.TRANSFORMER_COMPONENTS, 1));
        }
    }


    ///Doing Stuff

    //When progress >= 15360 (or 256*60), the charging is complete.
    float progress = 0;
    int tickTimer = 20;

    boolean canTransform = false;

    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- <= 0){
            tickTimer = 20;
            progress += Math.abs(getSpeed());
            if (progress >= 15360){
                progress = performCharge() ? 0 : 15104;
            }
            if (!canTransform && BusyItemHandler.getStackInSlot(0).is(AllItems.TRANSFORMER_COMPONENTS.get())){
                canTransform = true;
                BusyItemHandler.extractItem(0,1,false);
            }
        }
    }

    public boolean performCharge() {

        BlockEntity BE = level.getBlockEntity(getBlockPos().above());
        if (BE != null && BE.getType() == AllBlockEntityTypes.DEPOT.get()) {
            DepotBehaviour DPB = ((DepotBlockEntity) BE).getBehaviour(DepotBehaviour.TYPE);

            //Look For Heating
            BlockPos below = getBlockPos().below();
            float heat = BoilerHeater.findHeat(level, below, level.getBlockState(below));
            //Make ToInsert
            TransportedItemStack toInsert = new TransportedItemStack(AllBatteryItems.getBattery((int) heat+1).asStack(1));

            //Make sure the stack can fit into the depot
            if (DPB.insert(toInsert,true) != ItemStack.EMPTY){return false;}

            if (canTransform && heat >= 1 && BusyItemHandler.getStackInSlot(0).is(AllBatteryItems.getBatteryItem((int)heat)) && BusyItemHandler.getStackInSlot(0).getCount() >= 4) {
                //Transform
                DPB.insert(toInsert,false);
                BusyItemHandler.extractItem(0,4,false);
                return true;
            } else if (heat == -1 && BusyItemHandler.getStackInSlot(0).is(AllBatteryItems.getBatteryItem(0)) && BusyItemHandler.getStackInSlot(0).getCount() >= 1) {
                //Charge Empty
                DPB.insert(toInsert, false);
                BusyItemHandler.extractItem(0, 1, false);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.charger.progress").append(progress + "/ 15360")));
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.charger.transformer." +(canTransform ? "present" : "absent") )));
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        return true;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("tick_timer");
        progress = tag.getFloat("charge_progress");
        canTransform = tag.getBoolean("can_transform");
        BusyItemHandler.deserializeNBT(tag.getCompound("items"));
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("tick_timer",tickTimer);
        tag.putFloat("charge_progress",progress);
        tag.putBoolean("can_transform",canTransform);
        tag.put("items", BusyItemHandler.serializeNBT());
    }
}
