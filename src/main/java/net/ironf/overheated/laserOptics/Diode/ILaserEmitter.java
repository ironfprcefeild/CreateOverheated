package net.ironf.overheated.laserOptics.Diode;

import net.minecraft.world.level.Level;


//Block Entities implement this interface
//Those Block Entities must have a Laser Segment which is accsessed via methods below
//Those Block Entities must tick the laser segment
//The Update Laser Emmision method in the laser segment controls the laser
public interface ILaserEmitter {
    Level getLaserWorld();

}
