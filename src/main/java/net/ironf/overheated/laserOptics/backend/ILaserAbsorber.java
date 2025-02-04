package net.ironf.overheated.laserOptics.backend;

import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.Direction;

public interface ILaserAbsorber {
    //If a laser is firing on you, this will be called every 5 ticks, return true to indicate to pass through the block
    default boolean absorbLaser(Direction incoming, HeatData beamHeat, int distance, float eff){
        return false;
    }

}
