package net.ironf.overheated.nuclear.radiolyzer;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.gasses.IGasPlacer;
import net.ironf.overheated.nuclear.rods.ControlRodsRegister;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.ironf.overheated.utility.machines.CapableMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class RadiolyzerBlockEntity extends CapableMachineBlockEntity implements ControlRodsRegister.IControlRod, IGasPlacer, IHaveGoggleInformation {
    public RadiolyzerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /// Fluid Handling
    @Override
    public int getFluidCapacity() {
        return 4000;
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
                if (Tank().fill(toFill, IFluidHandler.FluidAction.SIMULATE) == 2000){
                    Tank().fill(toFill, IFluidHandler.FluidAction.EXECUTE);
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
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
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
