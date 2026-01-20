package net.ironf.overheated.cooling.chillChannel;

import net.minecraft.core.Direction;

public class MutableDirection {
    public Direction d;

    public MutableDirection(Direction immutable){
        this.d = immutable;
    }

    public Direction getImmutable() {
        return d;
    }

    public void setD(Direction newDir){
        d = newDir;
    }
}
