package net.ironf.overheated.steamworks.blocks.heatsink;

//Intended for block entities that want to detect air current
public interface IAirCurrentReader {
    void update(float strength);
}
