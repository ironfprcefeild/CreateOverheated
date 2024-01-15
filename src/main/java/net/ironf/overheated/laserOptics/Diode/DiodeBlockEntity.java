package net.ironf.overheated.laserOptics.Diode;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.ironf.overheated.laserOptics.backend.LaserAbsorber;
import net.ironf.overheated.laserOptics.backend.LaserGenerator;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class DiodeBlockEntity extends LaserGenerator implements LaserAbsorber {
    public DiodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public void absorb(HeatData heat, Direction sideHit) {
        LaserAbsorber.super.absorb(heat,sideHit);
    }

    @Override
    public HeatData laserToPush(Direction dir) {
        return super.laserToPush(dir);
    }

    //TODO make the laser diode work
}
