package net.ironf.overheated.steamworks.blocks.pressureHeater;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
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
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;
import static net.ironf.overheated.utility.GoggleHelper.heatTooltip;

public class PressureHeaterBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public PressureHeaterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    int timer = 8;
    HeatData recentReading = HeatData.empty();
    boolean useAllSteam = false;

    public void switchModes() {
        useAllSteam = !useAllSteam;
    }
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
            if (readHeat > 0) {
                int pressure = AllSteamFluids.getSteamPressure(input.getFluid());
                IFluidTank output = getTank(Direction.UP);
                int outputAmount = useAllSteam || output == null ? pressure + 1 : 1;
                recentReading = new HeatData(readHeat == 1 ? outputAmount : 0, readHeat == 2 ? outputAmount : 0, readHeat == 3 ? outputAmount : 0, 5f);
                if (!useAllSteam){
                    if (output != null)
                        output.fill(AllSteamFluids.getSteamFromValues(pressure,0,10), IFluidHandler.FluidAction.EXECUTE);
                }
                input.drain(10, IFluidHandler.FluidAction.EXECUTE);
            } else {
                recentReading = HeatData.empty();
            }
        }
    }

    public IFluidTank getTank(Direction in){
        BlockEntity be = level.getBlockEntity(getBlockPos().relative(in));
        return be instanceof FluidTankBlockEntity ? ((FluidTankBlockEntity) be).getControllerBE().getTankInventory() : null;
    }

    public HeatData getRecentReading(){
        return recentReading;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        heatTooltip(tooltip,recentReading, HeatDisplayType.SUPPLYING);
        tooltip.add(addIndent(Component.translatable("coverheated.pressure_heater.mode." + (useAllSteam ? "use" : "return"))));
        tooltip.add(addIndent(Component.translatable("coverheated.pressure_heater.mode.change")));
        return true;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        timer = tag.getInt("timer");
        recentReading = HeatData.readTag(tag,"heat");
        useAllSteam = tag.getBoolean("use_all");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",timer);
        HeatData.writeTag(tag,recentReading,"heat");
        tag.putBoolean("use_all",useAllSteam);
    }


}
