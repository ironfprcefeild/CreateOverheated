package net.ironf.overheated.cooling.chillChannel.core;

import net.minecraft.nbt.CompoundTag;

public class ChannelStatusBundle {
    public float maximumCooling = 0f;
    public float usedCooling = 0f;
    public ChannelStatusBundle(){
        maximumCooling = 0;
        usedCooling = 0;
    }
    public ChannelStatusBundle(CompoundTag t, String s){
        usedCooling = t.getFloat(s+"used");
        maximumCooling = t.getFloat(s+"maximum");
    }

    public void addLoad(float amount){
        usedCooling += amount;
    }
    public void addSource(float amount){
        maximumCooling += amount;
    }
    public float getDelta(){
        return maximumCooling - usedCooling;
    }
    public void reset(){
        this.maximumCooling = 0;
        this.usedCooling = 0;
    }
    public ChannelStatusBundle duplicate() {
        ChannelStatusBundle toReturn = new ChannelStatusBundle();
        toReturn.usedCooling = this.usedCooling;
        toReturn.maximumCooling = this.maximumCooling;
        return toReturn;
    }

    public void write(CompoundTag tag, String s){
        tag.putFloat(s+"maximum",maximumCooling);
        tag.putFloat(s+"used",usedCooling);
    }


}
