package net.ironf.overheated.steamworks.blocks.steamVent;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.ironf.overheated.steamworks.steamFluids.AllSteamFluids;
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
import java.lang.ref.WeakReference;
import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class steamVentBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public steamVentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        source = new WeakReference<>(null);
    }


    //Getting Boiler Tank (stolen from steam engine code)
    public WeakReference<FluidTankBlockEntity> source;
    public FluidTankBlockEntity getTank() {
        FluidTankBlockEntity tank = source.get();
        if (tank == null || tank.isRemoved()) {
            if (tank != null)
                source = new WeakReference<>(null);
            Direction facing = steamVentBlock.getAttachedDirection(getBlockState());
            BlockEntity be = level.getBlockEntity(worldPosition.relative(facing));
            if (be instanceof FluidTankBlockEntity tankBe)
                source = new WeakReference<>(tank = tankBe);
        }
        if (tank == null)
            return null;
        return tank.getControllerBE();
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


    //Doing Things

    float processingTicks = 75;
    boolean isSlow = false;
    @Override
    public void tick() {
        super.tick();
        FluidTankBlockEntity tank = getTank();
        if (tank != null) {
            BoilerData boiler = tank.boiler;
            if (boiler.isActive()) {
                processingTicks = processingTicks - 1;
                if (processingTicks < 1) {
                    if (!boiler.isPassive()){
                        setFluid(
                                AllSteamFluids.getSteamFromValues(
                                        ((int) (Math.floor((double) (boiler.activeHeat - 1) / 6) + 1)),
                                        0,
                                        getFluidStack().getAmount() + 1));
                    }
                    processingTicks = 75 + (5 *(boiler.attachedEngines - boiler.activeHeat));
                    isSlow = processingTicks != 75;

                }
            }
        }

    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.processingTicks = tag.getFloat("processing_ticks");
        this.isSlow = tag.getBoolean("is_slow");

    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putFloat("processing_ticks",this.processingTicks);
        tag.putBoolean("is_slow",this.isSlow);

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (isSlow) {
            tooltip.add(addIndent(Component.translatable("coverheated.steam_vent.slow")));
        }
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        return true;
    }


}
