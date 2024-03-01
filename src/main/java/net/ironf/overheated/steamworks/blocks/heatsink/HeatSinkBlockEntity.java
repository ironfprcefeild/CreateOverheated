package net.ironf.overheated.steamworks.blocks.heatsink;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.AllBlockEntities;
import net.ironf.overheated.utility.GoggleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class HeatSinkBlockEntity extends SmartBlockEntity implements IAirCurrentReader, IHaveGoggleInformation {
    public HeatSinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    ////Doing Stuff
    int timer = 5;
    float sunken = 0;

    @Override
    public void tick() {
        super.tick();
        if (timer >= 0){
            sunken = 0;
        } else {
            timer--;
        }
    }

    //This method is used by the IHeatSinkReader interface to collect the total heat sink of a series of heat sinks
    public float findTotalSunk(Direction in){
        float runningTotal = findSunken();
        int i = 1;
        while (i != 5){
            BlockEntity be = level.getBlockEntity(getBlockPos().relative(in,i));
            if (be != null && be.getType() == AllBlockEntities.HEAT_SINK.get()){
                runningTotal = runningTotal + ((HeatSinkBlockEntity) be).findSunken();
            } else {
                return runningTotal;
            }
            i++;
        }
        return runningTotal;
    }

    public float findSunken(){
        return sunken;
    }

    @Override
    public void update(float strength) {
        sunken = strength;
        timer = 5;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(GoggleHelper.addIndent(Component.literal(String.valueOf(findSunken())),1));
        return true;
    }
}
