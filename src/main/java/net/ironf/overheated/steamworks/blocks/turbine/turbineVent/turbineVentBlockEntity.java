package net.ironf.overheated.steamworks.blocks.turbine.turbineVent;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.steamworks.blocks.turbine.multiblock.turbineBlockEntity;
import net.ironf.overheated.steamworks.steamFluids.AllSteamFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import java.lang.ref.WeakReference;
import java.util.List;

public class turbineVentBlockEntity extends SmartBlockEntity {
    public turbineVentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        source = new WeakReference<>(null);
    }

    //Getting the turbine
    public WeakReference<turbineBlockEntity> source;
    public turbineBlockEntity getTurbine() {
        turbineBlockEntity turbineBlockEntity = source.get();
        if (turbineBlockEntity == null || turbineBlockEntity.isRemoved()) {
            if (turbineBlockEntity != null)
                source = new WeakReference<>(null);
            Direction facing = turbineVentBlock.getAttachedDirection(getBlockState());
            BlockEntity be = level.getBlockEntity(worldPosition.relative(facing));
            if (be instanceof turbineBlockEntity turbineBe)
                source = new WeakReference<>(turbineBlockEntity = turbineBe);
        }
        if (turbineBlockEntity == null)
            return null;
        return turbineBlockEntity.getControllerBE();
    }

    public int pushToTurbine(FluidStack steam){
        int pressureLevel = AllSteamFluids.getSteamPressure(steam);
        if (pressureLevel == 0){
            return 0;
        }

        turbineBlockEntity turbine = getTurbine();

        if (turbine.mbSteamIn >= turbine.getCapacities()){
            return 0;
        }

        if (turbine.currentPressure == pressureLevel && turbine.mbSteamIn != 0){
            turbine.mbSteamIn = turbine.mbSteamIn + steam.getAmount();
        } else if (turbine.mbSteamIn == 0){
            turbine.mbSteamIn = steam.getAmount();
            turbine.currentPressure = pressureLevel;
        } else {
            return 0;
        }
        return steam.getAmount();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }


}
