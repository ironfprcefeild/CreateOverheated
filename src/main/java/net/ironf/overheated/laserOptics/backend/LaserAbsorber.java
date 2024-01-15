package net.ironf.overheated.laserOptics.backend;

import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.Direction;

//Block-Entities which read the head of incoming lasers should implement this
public interface LaserAbsorber {
    default void absorb(HeatData heat, Direction sideHit){

    }
}
