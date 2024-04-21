package net.ironf.overheated.steamworks.blocks.pressureChamber.additions.steam;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.ChamberAdditionBlockEntity;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.ChamberAdditionType;
import net.ironf.overheated.steamworks.blocks.pressureChamber.additions.backend.IChamberAdditionBlockEntity;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

public class ChamberSteamBlockEntity extends ChamberAdditionBlockEntity implements IChamberAdditionBlockEntity {
    public ChamberSteamBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    //Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour tank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(tank = SmartFluidTankBehaviour.single(this, 2000).forbidInsertion());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.tank.getPrimaryHandler());

    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyFluidHandler.invalidate();
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return tank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }
    public void setFluid(FluidStack stack) {
        this.tank.getPrimaryHandler().setFluid(stack);
    }
    public FluidStack getFluidStack() {
        return this.tank.getPrimaryHandler().getFluid();
    }

    boolean isOutput = false;
    @Override
    public ChamberAdditionType getAdditionType() {
        return isOutput ? ChamberAdditionType.STEAM_OUT : ChamberAdditionType.STEAM_IN;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        isOutput = tag.getBoolean("mode");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("mode",isOutput);
    }

    public void changeType(){
        isOutput = !isOutput;
    }

    @Override
    public void otherGoggleInfo(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(GoggleHelper.addIndent(Component.translatable("coverheated.pressure_chamber.steam." + (isOutput ? "output" : "input"))));
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);

    }

}
