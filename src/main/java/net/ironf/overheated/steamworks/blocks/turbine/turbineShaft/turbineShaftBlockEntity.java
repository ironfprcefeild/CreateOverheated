package net.ironf.overheated.steamworks.blocks.turbine.turbineShaft;

import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.multiblock.turbineBlockEntity;
import net.ironf.overheated.steamworks.blocks.turbine.turbineVent.turbineVentBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.ref.WeakReference;

public class turbineShaftBlockEntity extends GeneratingKineticBlockEntity {
    public turbineShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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

    @Override
    public float getGeneratedSpeed() {
        //Speed is 1-256 based off of flow-through rate
        //If the output area is full or input area empty it locks up and produces no SU
        //To avoid this the intent is to get the right amount/speed of pumps to operate the turbine
        turbineBlockEntity turbine = getTurbine();
        if (turbine != null && turbine.mbSteamIn > 0 && turbine.mbSteamOut < turbine.getCapacities()){
            //Maximum turbine capacity is 270
            return Math.max(turbine.getCapacities() - 4, 1);
        }
        return super.getGeneratedSpeed();
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = 384f;
        this.lastCapacityProvided = capacity;
        return capacity;
    }


}
