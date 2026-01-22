package net.ironf.overheated.batteries.discharger;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.ironf.overheated.batteries.AllBatteryItems;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.blocks.turbine.turbineEnd.turbineEndBlock;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.HeatDisplayType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;
import static net.ironf.overheated.utility.GoggleHelper.easyFloat;

public class DischargerBlockEntity extends GeneratingKineticBlockEntity {
    public DischargerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
        ItemHelper.dropContents(getLevel(), getBlockPos(), BusyItemHandler);
    }

    /// Slot Box
    ScrollValueBehaviour targetSpeed;
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        Integer max = AllConfigs.server().kinetics.maxRotationSpeed.get();

        targetSpeed =
                new KineticScrollValueBehaviour(CreateLang.translateDirect("kinetics.speed_controller.rotation_speed"),
                        this, new DischargerSlotBox());
        targetSpeed.between(-max, max);
        targetSpeed.value = 16;
        targetSpeed.withCallback(i -> this.updateTargetRotation());
        behaviours.add(targetSpeed);
    }

    private void updateTargetRotation() {
        if (hasNetwork())
            getOrCreateNetwork().remove(this);
        reActivateSource = true;
        RotationPropagator.handleRemoved(level, worldPosition, this);
        removeSource();
        attachKinetics();

    }

    ///Doing Stuff
    float Duration = 0;
    float thermalDuration = 0;
    int ThermalDischargeLevel = 0;
    int tickTimer = 20;

    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- <= 0) {
            tickTimer = 20;
            if (thermalDuration > 0) {
                thermalDuration--;
            }
            if (Duration > 0) {
                Duration = Duration - Math.abs(getSpeed());
                if (Duration <= 0){
                    Duration = 0;
                    updateTargetRotation();
                }
            }
            if (thermalDuration + Duration == 0) {
                performDischarge();
            }
        }
    }



    public void performDischarge() {

        BlockEntity BE = level.getBlockEntity(getBlockPos().above());
        if (BE != null && BE.getType() == AllBlockEntityTypes.DEPOT.get()) {
            DepotBehaviour DPB = ((DepotBlockEntity) BE).getBehaviour(DepotBehaviour.TYPE);
            int Voltage = AllBatteryItems.voltageOf(BusyItemHandler.getStackInSlot(0));
            TransportedItemStack toInsert = new TransportedItemStack(AllBatteryItems.getBattery(Voltage - 1).asStack(Voltage == 1 ? 1 : 4));

            if (Voltage >= 1 && DPB.insert(toInsert,true) == ItemStack.EMPTY) {
                //Transform
                DPB.insert(toInsert,false);
                BusyItemHandler.extractItem(0,1,false);

                ThermalDischargeLevel = Voltage - 1;
                Duration = 15360f;
                thermalDuration = 60f;
                updateTargetRotation();
            }
        }
    }

    //Kinetics
    @Override
    public float getGeneratedSpeed() {
        return Duration > 0
                ? convertToDirection(targetSpeed.getValue(), getBlockState().getValue(turbineEndBlock.FACING))
                : 0f;
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = Duration > 0
            ? 64f
            : 0f;
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    public void initialize() {
        super.initialize();
        this.sendData();
        if (!this.hasSource() || this.getGeneratedSpeed() > this.getTheoreticalSpeed()) {
            this.updateGeneratedRotation();
        }
    }

    //Heating (this method is called from the diode heater registry)
    public HeatData getGeneratedHeat() {
        if (thermalDuration > 0){
            return new HeatData(ThermalDischargeLevel,1);
        }
        return HeatData.empty();
    }

    //Goggles
    public float getDuration(){
        return this.Duration;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        GoggleHelper.heatTooltip(tooltip,ThermalDischargeLevel > 0 ? new HeatData(ThermalDischargeLevel,1) : HeatData.empty(), HeatDisplayType.SUPPLYING);
        tooltip.add(addIndent(Component.translatable("coverheated.discharger.thermal_duration").append(easyFloat(thermalDuration) + "s")));
        tooltip.add(addIndent(Component.translatable("coverheated.discharger.kinetic_duration").append(getDuration()+"")));
        tooltip.add(addIndent(Component.translatable("coverheated.discharger.decreasing_by").append(Math.abs(getSpeed()) + "/s"),2));


        return true;
    }

    //Read/write
    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        Duration = tag.getFloat("duration");
        thermalDuration = tag.getFloat("thermal_duration");
        ThermalDischargeLevel = tag.getInt("thermal_level");
        BusyItemHandler.deserializeNBT(tag.getCompound("items"));
        tickTimer = tag.getInt("tick_timer");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("discharge", Duration);
        tag.putFloat("thermal_duration",thermalDuration);
        tag.putInt("thermal_level",ThermalDischargeLevel);
        tag.put("items", BusyItemHandler.serializeNBT());
        tag.putInt("tick_timer",tickTimer);
    }

}
