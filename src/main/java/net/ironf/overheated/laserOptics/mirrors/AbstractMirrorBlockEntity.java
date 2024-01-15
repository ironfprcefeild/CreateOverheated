package net.ironf.overheated.laserOptics.mirrors;

import net.ironf.overheated.laserOptics.backend.LaserAbsorber;
import net.ironf.overheated.laserOptics.backend.LaserGenerator;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractMirrorBlockEntity extends LaserGenerator implements LaserAbsorber {
    public AbstractMirrorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public HashMap<Direction, HeatData> outGoing = new HashMap<>();
    public HashMap<Direction,Direction> reflections = new HashMap<>();


    @Override
    public void absorb(HeatData heat, Direction sideHit) {
        outGoing.put(reflections.get(sideHit),heat);
    }

    @Override
    public HeatData laserToPush(Direction dir) {
        if (outGoing.containsKey(dir)){
            HeatData toReturn = outGoing.get(dir);
            outGoing.remove(dir);
            return toReturn;
        }
        return null;
    }
}
