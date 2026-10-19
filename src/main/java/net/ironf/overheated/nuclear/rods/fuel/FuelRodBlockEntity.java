package net.ironf.overheated.nuclear.rods.fuel;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.gasses.IGasPlacer;
import net.ironf.overheated.nuclear.rods.ControlRodsRegister;
import net.ironf.overheated.nuclear.rods.IMakeNeutrinos;
import net.ironf.overheated.utility.machines.CooledMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static net.ironf.overheated.utility.GoggleHelper.addIndent;
import static net.ironf.overheated.utility.GoggleHelper.easyFloat;

public class FuelRodBlockEntity extends CooledMachineBlockEntity implements
        IMakeNeutrinos, ControlRodsRegister.IControlRod, IGasPlacer, IHaveGoggleInformation {
    public FuelRodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Integer neutrinosStored = 0;
    public Integer neutrinoQueue = 0;
    public Integer saturationLimit = getInitialSaturationLimit();

    private Integer getInitialSaturationLimit() {
        return 8192;
    }

    @Override
    public void fifthTick() {
        super.fifthTick();
        neutrinosStored = Math.min(neutrinoQueue, saturationLimit);
        if (neutrinosStored != 0) {
            fireNeutrinosOrthogonally(neutrinosStored, getBlockPos(), level);
        }
        neutrinoQueue = 0;
        /*
        if (currentTemp >= getMeltDownLimit()){
            fireNeutrinosOrthogonally((int) currentTemp,getBlockPos(),level);
        }
        //TODO Add meltdown logic, should explode, place a block of corium, and create a bunch of radiation
         */
    }

    public float getMeltDownLimit() {
        return 10000;
    }

    @Override
    public boolean doCooling() {
        return true;
    }

    @Override
    public Integer regulate(int incomingNeutrinos, Direction direction, BlockPos pos, BlockState state, Level level) {
        Overheated.LOGGER.info("Regulating Neutrinos");
        neutrinoQueue += incomingNeutrinos;
        currentTemp += incomingNeutrinos;
        if (neutrinoQueue >= level.random.nextIntBetweenInclusive(0,neutrinosStored)* getLifeTimeCoefficient()){
            saturationLimit--;
        }
        return 0;
    }

    public float getLifeTimeCoefficient() {
        return 0.5f;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        neutrinoQueue = tag.getInt("n_queue");
        neutrinosStored = tag.getInt("n_stored");
        saturationLimit = tag.getInt("n_limit");

    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("n_queue",neutrinoQueue);
        tag.putInt("n_stored",neutrinosStored);
        tag.putInt("n_limit",saturationLimit);

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tempAndCoolInfo(tooltip);
        tooltip.add(Component.translatable("overheated.fuel_rod.neutrinos").append(String.valueOf(neutrinosStored)));
        tooltip.add(Component.translatable("overheated.fuel_rod.saturation_limit").append(String.valueOf(saturationLimit)));

        return true;
    }


}
