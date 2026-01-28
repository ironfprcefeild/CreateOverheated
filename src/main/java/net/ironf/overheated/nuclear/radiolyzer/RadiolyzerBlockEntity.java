package net.ironf.overheated.nuclear.radiolyzer;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.AllFluids;
import net.ironf.overheated.gasses.AllGasses;
import net.ironf.overheated.gasses.IGasPlacer;
import net.ironf.overheated.nuclear.rods.ControlRodsRegister;
import net.ironf.overheated.steamworks.AllSteamFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;

public class RadiolyzerBlockEntity extends SmartBlockEntity implements ControlRodsRegister.IControlRod, IGasPlacer, IHaveGoggleInformation {
    public RadiolyzerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

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

                directionChecked = (directionChecked+1)%4;
                placeGasBlock(getBlockPos().relative(Iterate.horizontalDirections[directionChecked]), AllGasses.hydrogen,level);
                directionChecked = (directionChecked+1)%4;
                placeGasBlock(getBlockPos().relative(Iterate.horizontalDirections[directionChecked]), AllGasses.hydrogen,level);
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
