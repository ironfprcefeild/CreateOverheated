package net.ironf.overheated.laserOptics.backend;

import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.Direction;

//Only block entities may implement this interface
public interface ILaserAbsorber {

    void setLaserTimer(int Timer, Direction d);
    int getLaserTimer(Direction d);
    void setLaserHD(HeatData hd, Direction d);
    default void laserTick(Direction d){
        if (getLaserTimer(d) == 0){
            setLaserHD(HeatData.empty(),d);
        } else {
            setLaserTimer(getLaserTimer(d) - 1,d);
        }
    }

    default boolean absorbLaser(Direction incoming, HeatData beamHeat, int distance, float eff){

        return false;
    }




}
