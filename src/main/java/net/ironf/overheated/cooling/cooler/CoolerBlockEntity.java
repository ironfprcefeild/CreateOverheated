package net.ironf.overheated.cooling.cooler;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.cooling.CoolingData;
import net.ironf.overheated.cooling.ICoolingBlockEntity;
import net.ironf.overheated.cooling.colants.CoolingHandler;
import net.ironf.overheated.utility.GoggleHelper;
import net.ironf.overheated.utility.SmartMachineBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CoolerBlockEntity extends SmartMachineBlockEntity implements ICoolingBlockEntity, IHaveGoggleInformation {
    public CoolerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 1000).allowExtraction().allowInsertion());
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

    //Cooling

    //We pass on all of the cooling data from adjacent cooling sources, with the efficiency of the stored coolant
    @Override
    public boolean doCooling() {
        return false;
    }

    @Override
    public CoolingData getGeneratedCoolingData(BlockPos myPos, BlockPos cooledPos, Level level, Direction in) {
        //Checks to ensure that the cooler is facing into the cooled block, we have coolant,and not cooling a cooler
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (facing.getOpposite() == in

            && (effTracker > 0)
            && (level.getBlockState(cooledPos).getBlock() != AllBlocks.COOLER.get())
            && (level.getBlockState(cooledPos).getBlock() != AllBlocks.CHANNEL_ABSORBER.get())) {

            return collectCooling(myPos,facing);
        } else {
            return CoolingData.empty();
        }
    }

    public CoolingData collectCooling(BlockPos myPos, Direction facing){
        //Gets cooling data from any adjacent blocks (besides the one being faced)
        ArrayList<Direction> directions = (new ArrayList<>(List.of(Iterate.directions)));
        CoolingData toReturn = getCoolingData(myPos, level, directions.toArray(Direction[]::new));

        Fluid coolant = tank.getPrimaryHandler().getFluid().getFluid();
        if (CoolingHandler.minTempHandler.containsKey(coolant)) {
            toReturn.minTemp = CoolingHandler.minTempHandler.get(coolant);
            toReturn.coolingUnits = effTracker * toReturn.coolingUnits;
            return toReturn;
        } else {
            return CoolingData.empty();
        }
    }




    //Doing Stuff

    public int tickTimer = 5;
    public float effTracker = 0;
    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- == 0){
            tickTimer = 75;
            tank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.EXECUTE);
            effTracker = !tank.isEmpty() ? CoolingHandler.efficiencyHandler.getOrDefault(tank.getPrimaryHandler().getFluid().getFluid(),0f) : 0f;
        }
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        tickTimer = tag.getInt("timer");
        effTracker = tag.getFloat("eff");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("timer",tickTimer);
        tag.putFloat("eff",effTracker);

    }

    //Goggles

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        if (effTracker > 0) {
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.cooler.header").withStyle(ChatFormatting.WHITE)));

            CoolingData cooled = collectCooling(getBlockPos(),getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.tooltip.cooling.cooling_units").withStyle(ChatFormatting.WHITE)));
            tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(cooled.coolingUnits)).withStyle(ChatFormatting.AQUA), 1));

            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.tooltip.cooling.min_temp").withStyle(ChatFormatting.WHITE)));
            tooltip.add(GoggleHelper.addIndent(Component.literal(GoggleHelper.easyFloat(cooled.minTemp)).withStyle(ChatFormatting.AQUA), 1));
        }else{
            tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.cooler.no_coolant").withStyle(ChatFormatting.WHITE)));
        }
        return true;
    }
}
