package net.ironf.overheated.steamworks.blocks.pressureHeater;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.HeatDisplayType;
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
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;
import static net.ironf.overheated.utility.GoggleHelper.heatTooltip;

public class PressureHeaterBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public PressureHeaterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
        lazyFluidHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }


    int timer = 8;
    HeatData recentReading = HeatData.empty();

    @Override
    public void tick() {
        super.tick();
        if (timer-- == 0){
            timer = 8;
            IFluidTank input = getTank(Direction.DOWN);
            if (input == null){
                recentReading = HeatData.empty();
                return;
            }
            int readHeat = AllSteamFluids.getSteamHeat(input.getFluid());
            if (readHeat > 0 && input.getFluidAmount() >= 10 && tank.getPrimaryHandler().getFluidAmount() <= 1990) {
                int pressure = AllSteamFluids.getSteamPressure(input.getFluid());
                recentReading = new HeatData(readHeat == 1 ? 1 : 0, readHeat == 2 ? 1 : 0, readHeat == 3 ? 1 : 0);
                tank.getPrimaryHandler().fill(AllSteamFluids.getSteamFromValues(pressure,0,10), IFluidHandler.FluidAction.EXECUTE);
                input.drain(10, IFluidHandler.FluidAction.EXECUTE);
            } else {
                recentReading = HeatData.empty();
            }
        }
    }

    public IFluidTank getTank(Direction in){
        BlockEntity be = level.getBlockEntity(getBlockPos().relative(in));
        if (be instanceof  FluidTankBlockEntity){
            FluidTankBlockEntity tank = ((FluidTankBlockEntity) be);
            if (tank.getControllerBE() != null){
                return tank.getControllerBE().getTankInventory();
            }
            return null;
        }
        return be instanceof FluidTankBlockEntity ? ((FluidTankBlockEntity) be).getControllerBE().getTankInventory() : null;
    }

    public HeatData getRecentReading(){
        return recentReading;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        heatTooltip(tooltip,recentReading, HeatDisplayType.SUPPLYING);
        return true;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        timer = tag.getInt("timer");
        recentReading = HeatData.readTag(tag,"heat");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",timer);
        HeatData.writeTag(tag,recentReading,"heat");
    }


}
