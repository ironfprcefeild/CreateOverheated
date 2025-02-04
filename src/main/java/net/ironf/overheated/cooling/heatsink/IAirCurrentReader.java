package net.ironf.overheated.cooling.heatsink;

import net.minecraft.core.Direction;

//Intended for block entities that want to detect air current
public interface IAirCurrentReader {
    void update(float strength, Direction incoming);
}
