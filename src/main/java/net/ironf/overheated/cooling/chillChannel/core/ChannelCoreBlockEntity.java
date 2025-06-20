package net.ironf.overheated.cooling.chillChannel.core;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import net.ironf.overheated.cooling.chillChannel.network.ChannelSlotBox;
import net.ironf.overheated.cooling.chillChannel.network.IChillChannelHook;
import net.ironf.overheated.cooling.colants.CoolingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public class ChannelCoreBlockEntity extends SmartBlockEntity implements IChillChannelHook, IHaveGoggleInformation {
    public ChannelCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int tickTimer = 20;
    public float currentEff = 0f;
    public boolean active = true;
    public BlockPos sendToPos = null;

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("ticktimer");
        currentEff = tag.getFloat("eff");
        active = tag.getBoolean("active");
        sendToPos = tag.getBoolean("sendtoset") ? BlockPos.of(tag.getLong("sendto")) : null;

    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("ticktimer",tickTimer);
        tag.putFloat("eff",currentEff);
        tag.putBoolean("active",active);
        if (sendToPos == null) {
            tag.putBoolean("sendtoset",false);
        } else {
            tag.putBoolean("sendtoset",true);
            tag.putLong("sendto",sendToPos.asLong());
        }
    }

    @Override
    public void tick() {
        super.tick();
        //Every Tick Timer, trigger this.
        //If core is invalid, next attempt will come sooner, otherwise there is a full minute delay
        if (tickTimer-- == 0){
            updateValidity();
            tickTimer = active ? 1200 : 20;
        }
    }

    //Sets Active to true if we have enough coolant, and the coolant fluid is valid fluid.
    //Sets efficenicy to coolant efficency if valid, otherwise sets it to 0.
    //If active is true, it then drains
    public void updateValidity(){
        if (active){
            tank.getPrimaryHandler().drain(capacityScrollWheel.getValue(), IFluidHandler.FluidAction.EXECUTE);
        }

        FluidStack coolantSupply = tank.getPrimaryHandler().getFluid();
        active =
                CoolingHandler.efficiencyHandler.containsKey(coolantSupply.getFluid())
                && tank.getPrimaryHandler().getFluid().getAmount() >= capacityScrollWheel.getValue();
        currentEff = active ? CoolingHandler.efficiencyHandler.get(coolantSupply.getFluid()) : 0;

    }
    ScrollValueBehaviour capacityScrollWheel;
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 8000).allowExtraction().allowInsertion());

        capacityScrollWheel =
                new ScrollValueBehaviour(Component.translatable("coverheated.chill_channel.core.scroll"), this, new ChannelSlotBox())
                        .between(1,256);

        behaviours.add(capacityScrollWheel);
    }
    @Override
    public int getBlockCapacity() {
        return active ? capacityScrollWheel.getValue() : 0;
    }

    @Override
    public float getCoolingUnitsCapacity() {
        return 0;
    }

    @Override
    public float getNetworkEfficency() {
        return active ? CoolingHandler.efficiencyHandler.get(tank.getPrimaryHandler().getFluid().getFluid()) : 0f;
    }

    @Override
    public float getNetworkMinTemp() {
        return active ? CoolingHandler.minTempHandler.get(tank.getPrimaryHandler().getFluid().getFluid()) : 10000f;
    }

    @Override
    public boolean canBeRouted() {
        return false;
    }
    @Override
    public void setDrawFrom(BlockPos bp) {}

    @Override
    public void unsetDrawFrom() {}


    @Override
    public void setSendToo(BlockPos bp) {
        if (sendToPos != null) {
            BlockEntity sbe = level.getBlockEntity(sendToPos);
            if (sbe instanceof IChillChannelHook oldSendingToo) {
                oldSendingToo.unsetDrawFrom();
            }
        }
        sendToPos = bp;
    }

    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.tank.getPrimaryHandler());
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }



    //Goggles


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
