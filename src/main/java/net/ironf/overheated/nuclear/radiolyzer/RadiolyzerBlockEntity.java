package net.ironf.overheated.nuclear.radiolyzer;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.gasses.IGasPlacer;
import net.ironf.overheated.nuclear.rods.ControlRodsRegister;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class RadiolyzerBlockEntity extends SmartBlockEntity implements ControlRodsRegister.IControlRod, IGasPlacer, IHaveGoggleInformation {
    public RadiolyzerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /// Fluid Handling
    /// Fluid Handling
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public SmartFluidTankBehaviour hydrogenTank;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(hydrogenTank = SmartFluidTankBehaviour.single(this, 4000).forbidInsertion().allowExtraction());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyFluidHandler = LazyOptional.of(() -> this.hydrogenTank.getPrimaryHandler());

    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyFluidHandler.invalidate();
    }

    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return hydrogenTank.getCapability().cast();
        }
        return super.getCapability(cap, side);
    }

    /// Processing
    int neutrinos = 0;
    int tickTimer = 0;
    int directionChecked = 0;

    @Override
    public void tick() {
        super.tick();
        if (tickTimer-- <= 0){
            tickTimer = 80;
            if (neutrinos >= 16){
                //Radiolyze!
                neutrinos = 0;
                for (Direction d : Iterate.horizontalDirections){
                    if (level.getFluidState(getBlockPos().relative(d)) != (AllSteamFluids.DISTILLED_WATER.SOURCE.get().getSource(false))){
                        //Not enough water surronding and such
                        return;
                    }
                }

                FluidStack toFill = new FluidStack(AllGasses.hydrogen.SOURCE.get(),2000);
                if (hydrogenTank.getPrimaryHandler().fill(toFill, IFluidHandler.FluidAction.SIMULATE) == 2000){
                    hydrogenTank.getPrimaryHandler().fill(toFill, IFluidHandler.FluidAction.EXECUTE);
                }

                directionChecked = (directionChecked+1)%4;
                level.setBlock(getBlockPos().relative(Iterate.horizontalDirections[directionChecked]), Blocks.AIR.defaultBlockState(),3);
                directionChecked = (directionChecked+1)%4;
                level.setBlock(getBlockPos().relative(Iterate.horizontalDirections[directionChecked]), Blocks.AIR.defaultBlockState(),3);
                directionChecked = (directionChecked+1)%4;
                placeGasBlock(getBlockPos().relative(Iterate.horizontalDirections[directionChecked]), AllGasses.oxygen,level);


            }
        }
    }

    @Override
    public Integer regulate(int incomingNeutrinos, Direction direction, BlockPos pos, BlockState state, Level level) {
        neutrinos += incomingNeutrinos;
        return 0;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        containedFluidTooltip(tooltip,isPlayerSneaking,lazyFluidHandler);
        tooltip.add(addIndent(Component.literal("Neu:" + neutrinos)));
        return true;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        neutrinos = tag.getInt("neu");
        tickTimer = tag.getInt("timer");
        directionChecked = tag.getInt("dir");

    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("neu",neutrinos);
        tag.putInt("timer",tickTimer);
        tag.putInt("dir",directionChecked);
    }
}
